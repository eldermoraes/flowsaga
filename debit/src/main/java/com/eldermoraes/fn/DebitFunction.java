/*
 * Copyright 2019 eldermoraes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eldermoraes.fn;

import com.eldermoraes.fn.messages.AmountReq;
import com.eldermoraes.fn.messages.TransactionRes;
import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.FnFeature;
import com.fnproject.fn.api.RuntimeContext;
import java.io.Serializable;
import com.fnproject.fn.api.flow.Flow;
import com.fnproject.fn.api.flow.FlowFuture;
import com.fnproject.fn.api.flow.Flows;
import com.fnproject.fn.runtime.flow.FlowFeature;

@FnFeature(FlowFeature.class)
public class DebitFunction implements Serializable{

    String funcDebit;
    String funcCredit;
    
    @FnConfiguration
    public void configure(RuntimeContext ctx) {
        funcDebit = ctx.getConfigurationByKey("DEBIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));
        
        funcCredit = ctx.getConfigurationByKey("CREDIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));
    }
    
    public void handleRequest(AmountReq input) {
        Flow f = Flows.currentFlow();
        
        FlowFuture<TransactionRes> debitFuture =
                f.invokeFunction(funcDebit, input.debit, TransactionRes.class);
        
        
    }

}