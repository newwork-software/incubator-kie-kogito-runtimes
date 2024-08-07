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
package org.jbpm.compiler.canonical;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory;
import org.jbpm.workflow.core.node.BoundaryEventNode;

import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_COMPENSATION;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_MESSAGE;
import static org.jbpm.ruleflow.core.Metadata.EVENT_TYPE_SIGNAL;
import static org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory.METHOD_ADD_COMPENSATION_HANDLER;
import static org.jbpm.ruleflow.core.factory.BoundaryEventNodeFactory.METHOD_ATTACHED_TO;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_EVENT_TYPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_INPUT_VARIABLE_NAME;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_SCOPE;
import static org.jbpm.ruleflow.core.factory.EventNodeFactory.METHOD_VARIABLE_NAME;

public class BoundaryEventNodeVisitor extends AbstractNodeVisitor<BoundaryEventNode> {

    public BoundaryEventNodeVisitor(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getNodeKey() {
        return "boundaryEventNode";
    }

    @Override
    public void visitNode(String factoryField, BoundaryEventNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, BoundaryEventNodeFactory.class, getNodeId(node), getNodeKey(), getWorkflowElementConstructor(node.getId())));
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getNameMethod(node, "BoundaryEvent"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_EVENT_TYPE, new StringLiteralExpr(node.getType())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_ATTACHED_TO, new StringLiteralExpr(node.getAttachedToNodeId())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_SCOPE, getOrNullExpr(node.getScope())));

        Variable variable = null;
        if (node.getVariableName() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_VARIABLE_NAME, new StringLiteralExpr(node.getVariableName())));
            variable = variableScope.findVariable(node.getVariableName());
        }
        if (node.getInputVariableName() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_INPUT_VARIABLE_NAME, new StringLiteralExpr(node.getInputVariableName())));
        }

        final String eventType = (String) node.getMetaData(EVENT_TYPE);
        if (EVENT_TYPE_SIGNAL.equals(eventType)) {
            metadata.addSignal(node.getType(), variable != null ? variable.getType().getStringType() : null);
        } else if (EVENT_TYPE_MESSAGE.equals(eventType)) {
            metadata.addTrigger(TriggerMetaData.of(node, node.getVariableName()));
        } else if (EVENT_TYPE_COMPENSATION.equalsIgnoreCase(eventType) && node.getAttachedToNodeId() != null) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_ADD_COMPENSATION_HANDLER, new StringLiteralExpr(node.getAttachedToNodeId())));
        }
        addNodeMappings(node, body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
