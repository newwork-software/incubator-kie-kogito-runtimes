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

import org.jbpm.process.core.context.variable.Mappable;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.process.instance.impl.ReturnValueEvaluator;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.kie.api.definition.process.WorkflowElementIdentifier;

public abstract class NodeFactory<T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> implements MappableNodeFactory<T> {
    public static final String METHOD_CONSTRAINT = "constraint";
    public static final String METHOD_NAME = "name";
    public static final String METHOD_METADATA = "metaData";
    public static final String METHOD_DONE = "done";

    protected Object node;
    protected NodeContainer nodeContainer;
    protected P nodeContainerFactory;

    protected NodeFactory(P nodeContainerFactory, NodeContainer nodeContainer, Object node, WorkflowElementIdentifier id) {
        this.nodeContainerFactory = nodeContainerFactory;
        this.nodeContainer = nodeContainer;
        this.node = node;
        setId(node, id);
        if (node instanceof Node) {
            nodeContainer.addNode((Node) node);
        }
    }

    protected void setId(Object node, WorkflowElementIdentifier id) {
        ((Node) node).setId(id);
    }

    public Node getNode() {
        return (Node) node;
    }

    public T name(String name) {
        getNode().setName(name);
        return (T) this;
    }

    public T metaData(String name, Object value) {
        getNode().setMetaData(name, value);
        return (T) this;
    }

    public P done() {
        return this.nodeContainerFactory;
    }

    @Override
    public Mappable getMappableNode() {
        return (Mappable) node;
    }

    public T constraint(WorkflowElementIdentifier toNodeId, String name, String type, String dialect, ReturnValueEvaluator evaluator, int priority) {
        return constraint(toNodeId, name, type, dialect, evaluator, priority, false);
    }

    public T constraint(WorkflowElementIdentifier toNodeId, String name, String type, String dialect, ReturnValueEvaluator evaluator, int priority, boolean isDefault) {
        ReturnValueConstraintEvaluator constraintImpl = new ReturnValueConstraintEvaluator();
        constraintImpl.setName(name);
        constraintImpl.setType(type);
        constraintImpl.setDialect(dialect);
        constraintImpl.setPriority(priority);
        constraintImpl.setEvaluator(evaluator);
        constraintImpl.setConstraint("expression already given as evaluator");
        constraintImpl.setDefault(isDefault);
        ((NodeImpl) node).addConstraint(new ConnectionRef(name, toNodeId, Node.CONNECTION_DEFAULT_TYPE), constraintImpl);
        return (T) this;
    }
}
