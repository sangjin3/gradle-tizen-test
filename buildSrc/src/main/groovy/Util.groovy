
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.StartParameter
import static groovy.io.FileType.FILES
import org.apache.tools.ant.taskdefs.condition.Os

class Util {
    public static String hostos;
    public static String sdk_path;
    public static String tizen_cmd;
    public static String sdb_cmd;
    public static String em_cli_cmd;
    public static String pwd;
    public static File trace_log = null;
    public static String task_name;

    public static void init(arg1, arg2){
        sdk_path = arg1;
        task_name = arg2;
        task_name = task_name.replaceAll('\\[','');
        task_name = task_name.replaceAll('\\]','');

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
            em_cli_cmd = "${sdk_path}/tools/emulator/bin/em-cli.bat";
        }else{
            tizen_cmd = "${sdk_path}/tools/ide/bin/tizen";
            sdb_cmd = "${sdk_path}/tools/sdb";
            em_cli_cmd = "${sdk_path}/tools/emulator/bin/em-cli";
        }

        File tizen_file  = new File(tizen_cmd);
        assert ( tizen_file.exists() );

        File sdb_file = new File(sdb_cmd);
        assert ( sdb_file.exists() );

        File em_cli_file = new File(em_cli_cmd);
        assert ( em_cli_file.exists() );

        //def date = new Date();
        //def formattedDate = date.format('yyyy-MM-dd-HH-mm-ss');
        if ( trace_log == null ){
            trace_log = new File("log/trace.log");
            if (trace_log.getParentFile() != null) {
                trace_log.getParentFile().mkdirs();
            }
            trace_log.createNewFile();
        }
    }

    public static void trace( test, int exit, StringBuilder sout, StringBuilder serr ){
        trace_log.append ("========================\n");
        trace_log.append ("${test} \n");
        trace_log.append ("exit code: ${exit} \n");
        trace_log.append ("stdout:---------------- \n");
        trace_log.append ("$sout \n");
        trace_log.append ("stderr:---------------- \n");
        trace_log.append ("$serr \n");
        trace_log.append ("----------------------- \n");
    }

    public static void log(String msg) {
        if ( trace_log == null ){
            trace_log = new File("log/trace.log");
            if (trace_log.getParentFile() != null) {
                trace_log.getParentFile().mkdirs();
            }
            trace_log.createNewFile();
        }

        trace_log.append(msg + " \n");
        println(msg);
    }

    public static int tizen_exec(test, args, OK_EXIT_VALUE, verbose){
        String command = "${tizen_cmd} ${args}";
        int exit;
        StringBuilder sout = new StringBuilder();
        StringBuilder serr = new StringBuilder();

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        exit = proc.exitValue();
        if( exit == OK_EXIT_VALUE ){
            log ("    Success: ${test}");
            if(verbose){
                log ("$sout");
                log ("$serr");
            }
        }else{
            log ("    Fail   : ${test} with exit value: " + exit);
            log ("stdout: $sout");
            log ("stderr: $serr");
        }
        //trace(test, exit, sout, serr);
        return exit;
    }

    public static int sdb_exec(test, args, OK_EXIT_VALUE, verbose){
        String command = "${sdb_cmd} ${args}";
        int exit;
        StringBuilder sout = new StringBuilder();
        StringBuilder serr = new StringBuilder();

        def proc = command.split().toList().execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        exit = proc.exitValue();
        if( exit == OK_EXIT_VALUE ){
            log ("    Success: ${test}");
            if(verbose){
                log ("$sout");
                log ("$serr");
            }
        }else{
            log ("    Fail   : ${test} with exit value: " + exit);
            log ("stdout: $sout");
            log (stderr: "$serr");
        }
        //trace(test, exit, sout, serr);
        return exit;
    }

    public static void sdb_exec_verify(test, args, OK_EXIT_VALUE, OK_STR, verbose){
        String command = "${sdb_cmd} ${args}";
        int found = 0;
        int exit;
        StringBuilder sout = new StringBuilder();
        StringBuilder serr = new StringBuilder();

        def proc = command.split().toList().execute();
        proc.waitForProcessOutput(sout, serr);

        sout.eachLine { line, count ->
            if ( line.contains("$OK_STR") ){
                found = 1;
            }
        }

        serr.eachLine { line, count ->
            if ( line.contains("$OK_STR") ){
                found = 1;
            }
        }

        exit = proc.exitValue();
        if( found && (exit == OK_EXIT_VALUE) ){
            log ("    Success: ${test}");
            if(verbose){
                log ("$sout");
                log ("$serr");
            }
        }else{
            log ("    Fail   : ${test} with exit:" + exit + " found:" + found);
            log ("stdout: $sout");
            log (stderr: "$serr");
        }

        //trace(test, exit, sout, serr);
    }
}

