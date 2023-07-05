package io.jenkins.plugins.implementation;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import java.io.IOException;
import java.text.Normalizer;

import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import io.jenkins.plugins.implementation.Deploy;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;

public class GetDetails extends Builder implements SimpleBuildStep {

    public String name;

    @DataBoundConstructor
    public GetDetails(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        listener.getLogger().println("Your WebHook URL " + name + "!");
        String apiToken = env.get("api-token");
        listener.getLogger().println(apiToken);
        String imageName = env.get("IMAGE_NAME");
        listener.getLogger().println(imageName);
        Deploy.sendPayload("dhananjay0106/jenkins_ci_pipeline:v13", name, listener, apiToken);
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.GetDetails_DescriptorImpl_DisplayName();
        }
    }

}
