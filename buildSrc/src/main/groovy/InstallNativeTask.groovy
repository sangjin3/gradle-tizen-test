
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class InstallNativeTest {
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

class InstallNativeTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;
    def serial;

    @TaskAction
        def test() {
            int i = 0;
            int total = 0;

            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("platform: ${platform}");
            println("serial: ${serial}");
            println("-------------------------------------");

            Util.init(sdk_path);

            InstallNativeTest.listTest(platform);

            i = 0;
            InstallNativeTest.AppList.each {
                //println(++i + " " + it.Name );
                ['x86'].each { arch ->
                    ['llvm'].each { compiler->
                        ['Debug'].each { configuration->
                            println ("   TC Path: [${Util.pwd}/out/${it.Platform}_${arch}_${compiler}_${configuration}]");
                            it.install(serial, "${arch}","${compiler}","${configuration}");
                        }
                    }
                }
                total = i;
                //println("Total template number: " + total);
            }
        }
}

