
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class CliConfig{
    public static void test(){
        def args = "cli-config -d default.profiles.path";
        Util.tizen_cmd("cli-config delete default.profile.path", args, 0);
    }
}

class Certificate{
    public static void create(){
        def args = "certificate --alias test_alias --password test_pwd -f test_key -- ${Util.sdk_path}/tools/ide/conf-ncli";
        Util.tizen_cmd("create certificate", args, 0);
    }
}

class SecurityProfile{
    public static void create(){
        def args = "security-profiles add -n test_alias -p est_pwd -a ${Util.sdk_path}/tools/ide/conf-ncli/test_key.p12";
        Util.tizen_cmd("create security-profile", args, 0);
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

            Util.init(sdk_path);
            CliConfig.test();
            Certificate.create();
            SecurityProfile.create();
        }
}

