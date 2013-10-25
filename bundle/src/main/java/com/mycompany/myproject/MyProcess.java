package com.mycompany.myproject;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Sample workflow process that sets an <code>approve</code> property to the payload based on the process argument value.
 */
@Component
@Service
@Properties({
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "A sample workflow process implementation."),
        @Property(name = Constants.SERVICE_VENDOR, value = "Awesome.com"),
        @Property(name = "process.label", value = "My Sample Workflow Process")})
public class MyProcess implements WorkflowProcess {

    private static final String TYPE_JCR_PATH = "JCR_PATH";

    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {
        WorkflowData workflowData = item.getWorkflowData();
        if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
            String path = workflowData.getPayload().toString() + "/jcr:content";
            try {
                Session jcrSession = session.getSession();
                Node node = (Node) jcrSession.getItem(path);
                if (node != null) {
                    node.setProperty("approved", readArgument(args));
                    jcrSession.save();
                }
            } catch (RepositoryException e) {
                throw new WorkflowException("Failed to set approved state", e);
            }
        }
    }

    private boolean readArgument(MetaDataMap args) {
        String argument = args.get("PROCESS_ARGS", "false");
        return argument.equalsIgnoreCase("true");
    }
}