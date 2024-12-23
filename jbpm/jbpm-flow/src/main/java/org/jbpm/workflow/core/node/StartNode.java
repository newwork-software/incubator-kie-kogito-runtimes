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
package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.api.definition.process.Connection;

import static org.jbpm.workflow.instance.WorkflowProcessParameters.WORKFLOW_PARAM_MULTIPLE_CONNECTIONS;

/**
 * Default implementation of a start node.
 * 
 */
public class StartNode extends ExtendedNodeImpl {

    private static final String[] EVENT_TYPES = new String[] { EVENT_NODE_EXIT };

    private static final long serialVersionUID = 510l;

    private List<Trigger> triggers;

    private boolean isInterrupting;

    private Timer timer;

    public StartNode() {
        setMetaData(Metadata.EVENT_TYPE, Metadata.EVENT_TYPE_NONE);
    }

    public void addTrigger(Trigger trigger) {
        if (triggers == null) {
            triggers = new ArrayList<>();
        }
        triggers.add(trigger);
    }

    public void removeTrigger(Trigger trigger) {
        if (triggers != null) {
            triggers.remove(trigger);
        }
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    @Override
    public String[] getActionTypes() {
        return EVENT_TYPES;
    }

    @Override
    public void validateAddIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
                "A start node [" + this.getUniqueId() + ", " + this.getName() + "] may not have an incoming connection!");
    }

    @Override
    public void validateRemoveIncomingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
                "A start node [" + this.getUniqueId() + ", " + this.getName() + "] may not have an incoming connection!");
    }

    @Override
    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        super.validateAddOutgoingConnection(type, connection);
        if (!Node.CONNECTION_DEFAULT_TYPE.equals(type)) {
            throw new IllegalArgumentException(
                    "A start node [" + this.getUniqueId() + ", " + this.getName() + "] only accepts default outgoing connection type!");
        }
        if (getTo() != null && !WORKFLOW_PARAM_MULTIPLE_CONNECTIONS.get(getProcess())) {
            throw new IllegalArgumentException(
                    "A start node [" + this.getUniqueId() + ", " + this.getName() + "] cannot have more than one outgoing connection!");
        }
    }

    public boolean isInterrupting() {
        return isInterrupting;
    }

    public void setInterrupting(boolean isInterrupting) {
        this.isInterrupting = isInterrupting;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

}
