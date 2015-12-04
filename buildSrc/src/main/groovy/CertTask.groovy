
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class CliConfig{
    public static void delete_default_security_profiles(){
        def args = "cli-config -d default.profiles.path";
        Util.tizen_exec("cli-config delete default.profile.path", args, 0, 0);
    }
    public static void set_default_security_profiles(){
        def args = "cli-config -g default.profiles.path=${Util.sdk_path}/tools/ide/conf-ncli/profiles.xml";
        Util.tizen_exec("cli-config set default.profile.path", args, 0, 0);
    }
}

class Certificate{
    public static void create(){
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
            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("-------------------------------------");

            Util.init(sdk_path, project.gradle.startParameter.taskNames);
            CliConfig.delete_default_security_profiles();
            Certificate.create();
            SecurityProfile.create();
        }
}

