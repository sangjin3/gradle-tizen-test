
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES
import org.apache.tools.ant.taskdefs.condition.Os

class Util {
    public static String hostos;
    public static String sdk_path;
    public static String tizen_cmd;
    public static String sdb_cmd;
    public static String pwd;

    private static StringBuilder sout = new StringBuilder();
    private static StringBuilder serr = new StringBuilder();

    public static void init(arg1){
        sdk_path = arg1;

        File dir = new File (".");
        pwd = dir.getCanonicalPath();

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            hostos = "win";
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            hostos = "mac";
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            hostos = "linux";
        }   

        if ( hostos  == "win" ) { 
            tizen_cmd = "${sdk_path}/tools/ide/bin/tizen.bat";
            sdb_cmd = "${sdk_path}/tools/sdb.exe";
        }else{
            tizen_cmd = "${sdk_path}/tools/ide/bin/tizen";
            sdb_cmd = "${sdk_path}/tools/sdb";
        }   

        File tizen_file  = new File(tizen_cmd);
        assert ( tizen_file.exists() );

        File sdb_file = new File(sdb_cmd);
        assert ( sdb_file.exists() );
    }

    public static void tizen_exec(test, args, OK_EXIT_VALUE, verbose){
        String command = "${tizen_cmd} ${args}";

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        if( proc.exitValue() == OK_EXIT_VALUE ){
            println ("       Success: ${test}");
            if(verbose){
                println ("$sout"); println ("$serr");
            }
        }else{

            println ("       Fail   : ${test} with exit value: " + proc.exitValue());
            println ("$sout"); println ("$serr");
        }

        sout.delete(0, sout.length()); serr.delete(0, serr.length()); 
    }

    public static void sdb_exec(test, args, OK_EXIT_VALUE, verbose){
        String command = "${sdb_cmd} ${args}";

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        if( proc.exitValue() == OK_EXIT_VALUE ){
            println ("       Success: ${test}");
            if(verbose){
                println ("$sout"); println ("$serr");
            }
        }else{
            println ("       Fail   : ${test} with exit value: " + proc.exitValue());
            println ("$sout"); println ("$serr");
        }

        sout.delete(0, sout.length()); serr.delete(0, serr.length()); 
    }

    public static void sdb_exec_verify(test, args, OK_EXIT_VALUE, OK_STR, verbose){
        String command = "${sdb_cmd} ${args}";
        int success = 0;

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        sout.eachLine { line, count ->
            if ( line.contains("$OK_STR") ){
                success = 1;
            }
        }

        if( success && (proc.exitValue() == OK_EXIT_VALUE) ){
            println ("       Success: ${test}");
            if(verbose){
                println ("$sout"); println ("$serr");
            }
        }else{
            println ("       Fail   : ${test} with exit value: " + proc.exitValue());
            println ("$sout"); println ("$serr");
        }

        sout.delete(0, sout.length()); serr.delete(0, serr.length()); 
    }
}

