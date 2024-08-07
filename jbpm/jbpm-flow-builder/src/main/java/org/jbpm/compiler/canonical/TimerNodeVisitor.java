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

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.factory.TimerNodeFactory;
import org.jbpm.workflow.core.node.TimerNode;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_DATE;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_DELAY;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_PERIOD;
import static org.jbpm.ruleflow.core.factory.TimerNodeFactory.METHOD_TYPE;

public class TimerNodeVisitor extends AbstractNodeVisitor<TimerNode> {

    public TimerNodeVisitor(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getNodeKey() {
        return "timerNode";
    }

    @Override
    public void visitNode(String factoryField, TimerNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        body.addStatement(getAssignedFactoryMethod(factoryField, TimerNodeFactory.class, getNodeId(node), getNodeKey(), getWorkflowElementConstructor(node.getId())))
                .addStatement(getNameMethod(node, "Timer"));

        Timer timer = node.getTimer();
        body.addStatement(getFactoryMethod(getNodeId(node), METHOD_TYPE, new IntegerLiteralExpr(timer.getTimeType())));

        if (timer.getTimeType() == Timer.TIME_CYCLE) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DELAY, new StringLiteralExpr(timer.getDelay())));
            if (timer.getPeriod() != null && !timer.getPeriod().isEmpty()) {
                body.addStatement(getFactoryMethod(getNodeId(node), METHOD_PERIOD, new StringLiteralExpr(timer.getPeriod())));
            }
        } else if (timer.getTimeType() == Timer.TIME_DURATION) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DELAY, new StringLiteralExpr(timer.getDelay())));
        } else if (timer.getTimeType() == Timer.TIME_DATE) {
            body.addStatement(getFactoryMethod(getNodeId(node), METHOD_DATE, new StringLiteralExpr(timer.getDate())));
        }

        addNodeMappings(node, body, getNodeId(node));
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
