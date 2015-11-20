
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class App {
    def Platform;
    def Template;
    def Name;

    App(arg1, arg2, arg3){
        Platform = arg1;
        Template = arg2;
        Name = arg3;
    }
}

class ListTest {
    def sdk_path;
    def profile;

    ArrayList<App> AppList;

    ListTest(arg1, arg2){
        sdk_path = arg1;
        profile = arg2;
        println("sdk_path: " + sdk_path);
        println("profile: " + profile);
    }

    def test() {
        def Platform;
        def Template;
        def Name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        AppList = new ArrayList<App>();

        def tizen = "${sdk_path}/tools/ide/bin/tizen";
        println("tizen path: " + tizen);

        def proc = ["${tizen}", "list", "web-project"].execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();
        if( proc.exitValue() == 0 ){
            println ("Success: list");
            //println ("$sout"); println ("$serr");
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

                def app = new App(Platform, Template, Name);
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

            logger.info("test_name: " + test_name);
            logger.info("sdk_path: " + sdk_path);
            logger.info("profile: " + profile);

            def list = new ListTest(sdk_path, profile);
            list.test();

            i = 0;
            list.AppList.each {
                i++;
                println(i + " " + it.Platform + " " + it.Template + " " + it.Name );
            }
            total = i;

            println("Total template number: " + total);
        }
}

