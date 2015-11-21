
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class WebApp {
    private String Platform;
    private String Template;
    private String Name;
    private String pwd;

    WebApp(arg1, arg2, arg3){
        Platform = arg1; Template = arg2; Name = arg3;

        File dir = new File (".");
        pwd = dir.getCanonicalPath();
    }

    def createTest(){
        def args = "create web-project ";
        args += "-p ${Platform} ";
        args += "-t ${Template} ";
        args += "-n ${Name} ";
        args += "-- ${pwd}/${Platform}";
        Util.tizen_cmd("create", args, 0);
    }

    def buildTest(){
        def args = "build-web ";
        args += "-- ${pwd}/${Platform}/${Name}";
        Util.tizen_cmd("build", args, 0);
    }

    def packageTest(){
        def args = "package ";
        args += "--type wgt ";
        args += "--sign test_alias  ";
        args += "-- ${pwd}/${Platform}/${Name}/.buildResult";
        Util.tizen_cmd("package", args, 0);
    }

    def checkWgt(){
        int success = 0;

        new File("${pwd}/${Platform}/${Name}").eachFileRecurse(FILES) {
            if( it.name.endsWith('.wgt') ){
                println ("       Success: ${Name}.wgt");
                success = 1;
            }
        }
        if (success == 0 ){
            println ("       Fail: ${Name}.wgt");
        }
    }
}

class WebTest {
    public static ArrayList<WebApp> AppList;

    public static void listTest(arg1) {
        def Platform;
        def Template;
        def Name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        AppList = new ArrayList<WebApp>();
        def profile = arg1;

        def proc = ["${Util.tizen_cmd}", "list", "web-project"].execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        if( proc.exitValue() == 0 ){
            println ("Success: list");
        }else{
            println ("Fail   : list");
            println ("$sout"); println ("$serr");
        }

        sout.eachLine { line, count ->
            if ( line.contains("${profile}") ){
                String[] splited = line.split("\\s+");
                Platform = splited[0];
                Template = splited[1];
                Name = splited[1].replaceAll('_','');
                Name = Name.replaceAll('-','');

                def app = new WebApp(Platform, Template, Name);
                AppList.add(app);
            }
        }
    }
}

class WebTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def profile;

    @TaskAction
        def test() {
            int i = 0;
            int total = 0;

            println("=====================================");
            println("${test_name}");
            println("profile: ${profile}");
            println("sdk path: ${sdk_path}");
            println("-------------------------------------");

            Util.init(sdk_path);

            WebTest.listTest(profile);

            i = 0;
            WebTest.AppList.each {
                println(++i + " " + it.Name );
                it.createTest();
                it.buildTest();
                it.packageTest();
                it.checkWgt();
            }
            total = i;
            //println("Total template number: " + total);
        }
}

