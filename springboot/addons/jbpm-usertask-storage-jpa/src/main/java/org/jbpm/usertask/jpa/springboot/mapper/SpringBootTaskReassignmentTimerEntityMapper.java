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

package org.jbpm.usertask.jpa.springboot.mapper;

import org.jbpm.usertask.jpa.mapper.TaskReassignmentTimerEntityMapper;
import org.jbpm.usertask.jpa.repository.TaskReassignmentTimerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringBootTaskReassignmentTimerEntityMapper extends TaskReassignmentTimerEntityMapper {

    public SpringBootTaskReassignmentTimerEntityMapper() {
        super(null);
    }

    @Autowired
    public SpringBootTaskReassignmentTimerEntityMapper(TaskReassignmentTimerRepository repository) {
        super(repository);
    }
}
