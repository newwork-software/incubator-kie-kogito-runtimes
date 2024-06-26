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
package org.jbpm.ruleflow.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.EndNode;
import org.kie.api.definition.process.WorkflowElementIdentifier;

import static org.jbpm.ruleflow.core.Metadata.ACTION;

public class EndNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends NodeFactory<EndNodeFactory<T>, T> implements SupportsAction<EndNodeFactory<T>, T> {

    public static final String METHOD_TERMINATE = "terminate";
    public static final String METHOD_ACTION = "action";

    public EndNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, new EndNode(), id);
    }

    protected EndNode getEndNode() {
        return (EndNode) getNode();
    }

    public EndNodeFactory<T> terminate(boolean terminate) {
        getEndNode().setTerminate(terminate);
        return this;
    }

    @Override
    public EndNodeFactory<T> action(Action action) {
        DroolsAction droolsAction = new DroolsAction();
        droolsAction.setMetaData(ACTION, action);
        List<DroolsAction> enterActions = getEndNode().getActions(ExtendedNodeImpl.EVENT_NODE_ENTER);
        if (enterActions == null) {
            enterActions = new ArrayList<>();
            getEndNode().setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, enterActions);
        }
        enterActions.add(droolsAction);
        return this;
    }
}
