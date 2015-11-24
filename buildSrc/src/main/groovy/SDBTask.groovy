
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class sdb_basic{

    public static void test(){
        String args = "";

        println("Basic SDB test");
        println ("   TC:");

        args = "";
        Util.sdb_exec("run test", args, 1, 0);

        args = "asdf";
        if ( Util.hostos  == "win" ) {
            Util.sdb_exec("test invalid parameter 'asdf'", args, -1, 0);
        }else{
            Util.sdb_exec("test invalid parameter 'asdf'", args, 255, 0);
        }

        args = "help";
        Util.sdb_exec("test help", args, 1, 0);

        args = "version";
        Util.sdb_exec_verify("test version", args, 0, "version", 0);
    }
}

class sdb_server{

    public static void test(){
        String args = "";

        println("SDB server test");
        println ("   TC:");

        args = "kill-server";
        Util.sdb_exec("test kill-server", args, 0, 0);

        args = "start-server";
        Util.sdb_exec("test start-server", args, 0, 0);
    }
}

class sdb_state{

    public static void test(){
        String args = "";

        println("SDB test with device");
        println ("   TC:");

        args = "get-serialno";
        Util.sdb_exec("get-serialno", args, 0, 0);

        args = "get-state";
        Util.sdb_exec("get-state", args, 0, 0);

        args = "get-state";
        Util.sdb_exec_verify("test get-state with return string", args, 0, "device", 0);

    }
}

class SDBTask extends DefaultTask {
    def test_name;
    def sdk_path;

    @TaskAction
        def test() {
            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("-------------------------------------");

            Util.init(sdk_path);

            // test with no emulator/device
            sdb_basic.test();
            sdb_state.test();

            sdb_server.test();

            // test with just one emulator or device
        }
}

