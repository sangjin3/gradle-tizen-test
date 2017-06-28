
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class NativeApp {
    private String Platform;
    private String Template;
    private String Name;

    NativeApp(arg1, arg2, arg3){
        Platform = arg1; Template = arg2; Name = arg3;
    }

    def createTest(arch, compiler, configuration){
        def args = "create native-project ";
        args += "-p ${Platform} ";
        args += "-t ${Template} ";
        args += "-n ${Name} ";
        args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}";
        Util.tizen_exec("create", args, 0, 0);

        File f = new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/Build");
        assert ( f.exists() );
    }

    def buildTest(arch, compiler, configuration){
        def args = "build-native ";
        args += "--arch ${arch} ";
        args += "--compiler ${compiler} ";
        args += "--configuration ${configuration} ";
        args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}";
        Util.tizen_exec("build", args, 0, 0);
    }

    def cleanTest(arch, compiler, configuration){
        def args = "clean ";
        args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}";
        Util.tizen_exec("clean", args, 0, 0);

        File f = new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}");
        assert ( ! f.exists() );
    }

    def packageTest(arch, compiler, configuration){
        int skip = 0;

        new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}").eachFileRecurse(FILES) {
            if( it.name.endsWith('.so') ||  it.name.endsWith('.a')){
                skip = 1;
            }
        }

        if ( skip == 0 ){
            def args = "package ";
            args += "--type tpk ";
            args += "--sign test_alias ";
            args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}";
            Util.tizen_exec("package", args, 0, 0);
        }else{
            println ("       Skip: package for ${Name}");
        }
    }

    def checkTpk(arch, compiler, configuration){
        int success = 0;

        new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}").eachFileRecurse(FILES) {
            if( it.name.endsWith('.so') ||  it.name.endsWith('.a')
                    ||  it.name.endsWith('.tpk') ){
                println ("       Success: $it.name");
                success = 1;
            }
        }

        if (success == 0 ){
            println ("       Fail: ${Name}");
        }
    }

    def reinstallTest(serial, arch, compiler, configuration){

        new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}").eachFileRecurse(FILES) {

            if( it.name.endsWith('.tpk') && it.name.contains('ui')){
                def args;

                XmlParser parser = new XmlParser();
                def manifest = parser.parse( new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/tizen-manifest.xml") );
                def pkgid = manifest.'ui-application'.'@appid'.toString();
                pkgid = pkgid.replaceAll('\\[','');
                pkgid = pkgid.replaceAll('\\]','');

                args = "package ";
                args += "--type tpk "; 
                args += "--sign test_alias ";
                args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}/${it.name}";
                Util.tizen_exec("package with resign", args, 0, 0);

                args = "install ";
                args += "--name ${it.name} ";
                args += "--serial ${serial} ";
                args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}";
                Util.tizen_exec("install ${Name}", args, 0, 0);

                args = "run ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("run ${Name}", args, 0, 0);

                sleep(1000);

                args = "uninstall ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("uninstall ${Name}", args, 0, 0);

            }else if( it.name.endsWith('.tpk') && it.name.contains('service')){
                println ("       Skip: package with resign $it.name");
            }else if( it.name.endsWith('.so') ||  it.name.endsWith('.a') ){
                println ("       Skip: package with resign $it.name");
            }
        }
    }

    def installTest(serial, arch, compiler, configuration){

        new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}").eachFileRecurse(FILES) {

            if( it.name.endsWith('.tpk') && it.name.contains('ui')){
                def args;

                XmlParser parser = new XmlParser();
                def manifest = parser.parse( new File("${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/tizen-manifest.xml") );
                def pkgid = manifest.'ui-application'.'@appid'.toString();
                pkgid = pkgid.replaceAll('\\[','');
                pkgid = pkgid.replaceAll('\\]','');

                args = "install ";
                args += "--name ${it.name} ";
                args += "--serial ${serial} ";
                args += "-- ${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}";
                Util.tizen_exec("install ${Name}", args, 0, 0);

                args = "run ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("run ${Name}", args, 0, 0);

                sleep(1000);

                args = "uninstall ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("uninstall ${Name}", args, 0, 0);

                args = "-s ${serial} ";
                args += "install ";
                args += "${Util.pwd}/out/${Platform}_${arch}_${compiler}_${configuration}/${Name}/${configuration}/${it.name}";
                Util.sdb_exec("sdb install ${Name}", args, 0, 0);

                args = "-s ${serial} ";
                args += "uninstall ";
                args += "${pkgid} ";
                Util.sdb_exec("sdb uninstall ${Name}", args, 0, 0);

            }else if( it.name.endsWith('.tpk') && it.name.contains('service')){
                println ("       Skip: $it.name");
            }else if( it.name.endsWith('.so') ||  it.name.endsWith('.a') ){
                println ("       Skip: $it.name");
            }
        }
    }

}

class NativeTest {
    public static ArrayList<NativeApp> AppList;

    public static void listTest(arg1) {
        def Platform;
        def Template;
        def Name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        AppList = new ArrayList<NativeApp>();
        def platform = arg1;

        def proc = ["${Util.tizen_cmd}", "list", "native-project"].execute();
        proc.waitForProcessOutput(sout, serr);

        if( proc.exitValue() == 0 ){
            println ("Success: list");
        }else{
            println ("Fail   : list");
            println ("$sout"); println ("$serr");
        }

        sout.eachLine { line, count ->
            if ( line.contains("${platform} ") ){
                String[] splited = line.split("\\s+");
                Platform = splited[0];
                Template = splited[1];
                Name = splited[1].replaceAll('_','');
                Name = Name.replaceAll('-','');

                def app = new NativeApp(Platform, Template, Name);
                AppList.add(app);
            }
        }
    }
}

class NativeTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;

    @TaskAction
        def test() {
            int i = 0;
            int total = 0;

            println("=====================================");
            println("${test_name}");
            println("platform: ${platform}");
            println("sdk path: ${sdk_path}");
            println("-------------------------------------");

            Util.init(sdk_path, project.gradle.startParameter.taskNames);

            NativeTest.listTest(platform);

            i = 0;
            NativeTest.AppList.each {
                println(++i + " " + it.Name );
                /*
                   println ("   TC: $it.Platform, $arch, $compiler, $configuration");
                   it.createTest("x86","gcc","Debug");
                   it.buildTest("x86","gcc","Debug");
                   it.packageTest("x86","gcc","Debug");
                   it.checkTpk("x86","gcc","Debug");
                 */

                ['x86', 'arm'].each { arch ->
                    ['gcc', 'llvm'].each { compiler->
                        ['Debug', 'Release'].each { configuration->
                            println ("   TC Path: [${Util.pwd}/out/${it.Platform}_${arch}_${compiler}_${configuration}]");
                            it.createTest  ("${arch}","${compiler}","${configuration}");
                            it.buildTest   ("${arch}","${compiler}","${configuration}");
                            it.packageTest ("${arch}","${compiler}","${configuration}");
                            it.checkTpk    ("${arch}","${compiler}","${configuration}");
                        }
                    }
                }
            }
            total = i;
            //println("Total template number: " + total);
        }
}

