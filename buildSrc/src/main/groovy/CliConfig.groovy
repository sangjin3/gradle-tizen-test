
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

