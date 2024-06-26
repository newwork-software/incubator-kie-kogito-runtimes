/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.workflow.instance.node;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.WorkflowElementIdentifier;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class EndNodeInstanceTest extends AbstractBaseTest {

    private static WorkflowElementIdentifier one = WorkflowElementIdentifierFactory.fromExternalFormat("one");
    private static WorkflowElementIdentifier two = WorkflowElementIdentifierFactory.fromExternalFormat("two");

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testEndNode() {
        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory factory = new MockNodeInstanceFactory(new MockNodeInstance(mockNode));
        NodeInstanceFactoryRegistry.getInstance(kruntime.getKieRuntime().getEnvironment()).register(mockNode.getClass(), factory);

        WorkflowProcessImpl process = new WorkflowProcessImpl();
        Node endNode = new EndNode();
        endNode.setId(one);
        endNode.setName("end node");

        mockNode.setId(two);
        new ConnectionImpl(mockNode, Node.CONNECTION_DEFAULT_TYPE, endNode, Node.CONNECTION_DEFAULT_TYPE);

        process.addNode(mockNode);
        process.addNode(endNode);

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setId("1223");
        processInstance.setState(ProcessInstance.STATE_ACTIVE);
        processInstance.setProcess(process);
        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) kruntime.getKieSession());

        MockNodeInstance mockNodeInstance = (MockNodeInstance) processInstance.getNodeInstance(mockNode);
        mockNodeInstance.triggerCompleted();
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
}
