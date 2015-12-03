
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class sdb_basic{

    public static void test(){
        String args = "";

        println("Basic test");
        println ("   TC:");

        args = "";
        Util.sdb_exec("test: no arguments", args, 1, 0);

        args = "asdf";
        if ( Util.hostos  == "win" ) {
            Util.sdb_exec("test: invalid argument 'asdf'", args, -1, 0);
        }else{
            Util.sdb_exec("test: invalid argument 'asdf'", args, 255, 0);
        }

        args = "help";
        Util.sdb_exec("test: help", args, 1, 0);

        args = "version";
        Util.sdb_exec_verify("test: version", args, 0, "version", 0);
    }
}

class sdb_server{

    public static void test(){
        String args = "";

        println("SDB server test");
        println ("   TC:");

        args = "kill-server";
        Util.sdb_exec("test: kill-server", args, 0, 0);

        args = "start-server";
        Util.sdb_exec("test: start-server", args, 0, 0);
    }
}

class sdb_state{

    public static void test(String serial){
        String args = "";

        println("state test");
        println ("   TC:");

        args = "get-serialno";
        Util.sdb_exec("test: get-serialno", args, 0, 0);

        args = "get-state";
        Util.sdb_exec("test: get-state", args, 0, 0);

        if(serial == null){
            println ("       Skip test get-state with return string");
        }else{
            args = "devices";
            Util.sdb_exec_verify("test: devices for checking ${serial}", args, 0, "${serial}", 0);
            Util.sdb_exec_verify("test: devices for checking 'device' ", args, 0, "device", 0);

            args = "-s ${serial} get-state";
            Util.sdb_exec_verify("test: get-state with return string", args, 0, "device", 0);

            args = "-s ${serial} get-serialno";
            Util.sdb_exec_verify("test: get-serialno for checking ${serial}", args, 0, "${serial}", 0);
        }
    }
}

class sdb_connect{

    public static void test(String serial){
        String args = "";

        println("SDB test connection");
        println ("   TC:");

        if(serial == null){
            println ("       Skip test");
        }else{
            args = "-s ${serial} forward tcp:7777 tcp:26101";
            Util.sdb_exec("test: forward", args, 0, 0);

            // scenario #1
            args = "-s ${serial} connect localhost:7777";
            Util.sdb_exec("test: connect", args, 0, 0);

            args = "-s localhost:7777 get-serialno";
            Util.sdb_exec_verify("test: get-serialno for checking localhost:7777", args, 0, "localhost:7777", 0);

            args = "-s ${serial} disconnect localhost:7777";
            Util.sdb_exec("test: disconnect", args, 0, 0);

            // scenario #2
            args = "-s ${serial} connect localhost:7777";
            Util.sdb_exec("test: connect", args, 0, 0);
            args = "-s ${serial} connect localhost:7777";
            Util.sdb_exec_verify("test: re-connect", args, 0, "localhost:7777 is already connected", 0);

            args = "-s ${serial} disconnect localhost:7777";
            Util.sdb_exec("test: disconnect", args, 0, 0);
        }
    }
}

class sdb_shell{

    public static void test(String serial){
        String args = "";

        println("shell test");
        println ("   TC:");

        if(serial == null){
            println ("       Skip test");
        }else{
            args = "-s ${serial} shell whoami";
            Util.sdb_exec_verify("test: shell whoami", args, 0, "developer", 0);
        }
    }
}

class sdb_push{

    public static void test(String serial){
        String args = "";

        println("push test");
        println ("   TC:");

        if(serial == null){
            println ("       Skip test");
        }else{
            args = "-s ${serial} push ${Util.pwd}/gradle/wrapper/gradle-wrapper.jar /home/developer";
            Util.sdb_exec_verify("test: push", args, 0, "pushed", 0);
        }
    }
}
class sdb_pull {

    public static void test(String serial){
        String args = "";

        println("pull test");
        println ("   TC:");

        if(serial == null){
            println ("       Skip test");
        }else{
            args = "-s ${serial} pull /home/developer/gradle-wrapper.jar ${Util.pwd}";
            Util.sdb_exec_verify("test: push", args, 0, "pulled", 0);
        }
    }
}

class SDBTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;
    def serial;

    @TaskAction
        def test() {
            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("platform: ${platform}");
            println("serial number: ${serial}");
            println("-------------------------------------");

            Util.init(sdk_path);

            sdb_basic.test();
            sdb_state.test(serial);
            sdb_connect.test(serial);
            sdb_shell.test(serial);
            sdb_push.test(serial);
            sdb_pull.test(serial);

            sdb_server.test();
        }
}
