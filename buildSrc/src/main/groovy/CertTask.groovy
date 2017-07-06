
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class Certificate{
    public static void create(){

        File test_key = null;
        test_key = new File("${Util.sdk_path}/tools/ide/conf-ncli/test_key.p12");
        if ( test_key.exists() ){
            test_key.delete();
        }

        def args = "certificate ";
        args += "--alias test_alias ";
        args += "--password test_pwd ";
        args += "-f test_key ";
        args += "-- ${Util.sdk_path}/tools/ide/conf-ncli";
        Util.tizen_exec("create certificate", args, 0, 0);
    }
}

class SecurityProfile{

    // created in ~/tizen-sdk-data/ide/keystore/profiles.xml
    public static void create(){
        def args = "security-profiles ";
        args += "add ";
        args += "-n test_alias ";
        args += "-p test_pwd ";
        args += "-a ${Util.sdk_path}/tools/ide/conf-ncli/test_key.p12";
        Util.tizen_exec("create security-profile", args, 0, 0);
    }
}

class CertTask extends DefaultTask {
    def test_name;
    def sdk_path;

    @TaskAction
        def test() {
            Util.init(sdk_path, project.gradle.startParameter.taskNames);

            Util.log("=====================================");
            Util.log("${test_name}");
            Util.log("sdk path: ${sdk_path}");
            Util.log("-------------------------------------");

            CliConfig.delete_default_security_profiles();
            Certificate.create();
            SecurityProfile.create();
        }
}

