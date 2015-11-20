
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

class App {
    def sdk_path;
    def Platform;
    def Template;
    def Name;
    def tizen_cmd;
    def pwd;
    def sout = new StringBuilder();
    def serr = new StringBuilder();

    App(arg1, arg2, arg3, arg4){
        sdk_path = arg1;
        Platform = arg2;
        Template = arg3;
        Name = arg4;

        tizen_cmd = "${sdk_path}/tools/ide/bin/tizen";
        File dir = new File (".");
        pwd = dir.getCanonicalPath();
    }

    def exec_cmd(test, args){
        def command = "${tizen_cmd} ${args}";

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        if( proc.exitValue() == 0 ){
            println ("       Success: ${test}");
        }else{
            println ("       Fail   : ${test}");
            println ("$sout"); println ("$serr");
        }

        sout.delete(0, sout.length()); serr.delete(0, serr.length()); 
    }

    def createTest(){
        def args = "create web-project -p ${Platform} -t ${Template} -n ${Name} -- ${pwd}/${Platform}";
        exec_cmd("create", args);
    }

    def buildTest(){
        def args = "build-web -- ${pwd}/${Platform}/${Name}";
        exec_cmd("build", args);
    }

    def packageTest(){
        def args = "package --type wgt --sign test_alias -- ${pwd}/${Platform}/${Name}/.buildResult";
        exec_cmd("package", args);
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
    def sdk_path;
    def profile;
    ArrayList<App> AppList;

    WebTest(arg1, arg2){
        sdk_path = arg1; profile = arg2;
    }

    def listTest() {
        def Platform;
        def Template;
        def Name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        AppList = new ArrayList<App>();

        def tizen_cmd = "${sdk_path}/tools/ide/bin/tizen";

        def proc = ["${tizen_cmd}", "list", "web-project"].execute();
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

                def app = new App(sdk_path, Platform, Template, Name);
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

            logger.info("${test_name}: ${profile}: ${sdk_path}");

            def web = new WebTest(sdk_path, profile);
            web.listTest();

            i = 0;
            web.AppList.each {
                println(++i + " " + it.Name );
                it.createTest();
                it.buildTest();
                it.packageTest();
                it.checkWgt();
            }
            total = i;
            println("Total template number: " + total);
        }
}

