/*
 * The MIT License
 *
 * Copyright (C) 2010-2011 by Anthony Robinson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.publish_over_ftp;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.io.IOException;
import java.util.ArrayList;
import jenkins.model.Jenkins;
import jenkins.plugins.publish_over.BPPlugin;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

@SuppressWarnings("PMD.LooseCoupling") // serializable
public class BapFtpPromotionPublisherPlugin extends Notifier implements SimpleBuildStep {

    private final BapFtpPublisherPlugin delegate;

    @DataBoundConstructor
    public BapFtpPromotionPublisherPlugin(
            final ArrayList<BapFtpPublisher> publishers,
            final boolean continueOnError,
            final boolean failOnError,
            final boolean alwaysPublishFromMaster,
            final String masterNodeName,
            final BapFtpParamPublish paramPublish) {
        this.delegate = new BapFtpPublisherPlugin(
                publishers, continueOnError, failOnError, alwaysPublishFromMaster, masterNodeName, paramPublish);
    }

    public BapFtpPublisherPlugin getDelegate() {
        return delegate;
    }

    @Override
    public void perform(
            @NonNull Run<?, ?> run, @NonNull FilePath ws, @NonNull Launcher launcher, @NonNull TaskListener listener)
            throws InterruptedException, IOException {
        delegate.perform(run, ws, launcher, listener);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return delegate.getRequiredMonitorService();
    }

    protected HashCodeBuilder addToHashCode(final HashCodeBuilder builder) {
        return builder.append(delegate);
    }

    protected EqualsBuilder addToEquals(final EqualsBuilder builder, final BapFtpPromotionPublisherPlugin that) {
        return builder.append(delegate, that.delegate);
    }

    protected ToStringBuilder addToToString(final ToStringBuilder builder) {
        return builder.append("delegate", delegate);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        return addToEquals(new EqualsBuilder(), (BapFtpPromotionPublisherPlugin) that)
                .isEquals();
    }

    public int hashCode() {
        return addToHashCode(new HashCodeBuilder()).toHashCode();
    }

    public String toString() {
        return addToToString(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE))
                .toString();
    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return BPPlugin.PROMOTION_JOB_TYPE.equals(aClass.getCanonicalName());
        }

        public String getDisplayName() {
            return Messages.promotion_descriptor_displayName();
        }

        public BapFtpPublisherPlugin.Descriptor getPublisherDescriptor() {
            return Jenkins.getInstance().getDescriptorByType(BapFtpPublisherPlugin.Descriptor.class);
        }
    }
}
