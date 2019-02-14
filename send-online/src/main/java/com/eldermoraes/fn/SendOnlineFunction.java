/*
 * Copyright (C) 2019 eldermoraes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.eldermoraes.fn;

import com.eldermoraes.fn.messages.MessageReq;
import com.eldermoraes.fn.messages.SendOnlineReq;
import com.eldermoraes.fn.messages.SendOnlineRes;
import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.FnFeature;
import com.fnproject.fn.api.RuntimeContext;
import com.fnproject.fn.api.flow.Flow;
import com.fnproject.fn.api.flow.FlowFuture;
import com.fnproject.fn.api.flow.Flows;
import com.fnproject.fn.runtime.flow.FlowFeature;

@FnFeature(FlowFeature.class)
public class SendOnlineFunction {

    String funcDebit;
    String funcCredit;
    String funcInternetBanking;

    @FnConfiguration
    public void configure(RuntimeContext ctx) {
        funcDebit = ctx.getConfigurationByKey("DEBIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

        funcCredit = ctx.getConfigurationByKey("CREDIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

        funcInternetBanking = ctx.getConfigurationByKey("INTERNET-BANKING-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

    }

    public void send(SendOnlineReq input) {

        Flow f = Flows.currentFlow();

        FlowFuture<SendOnlineRes> debitFuture
                = f.invokeFunction(funcDebit, input.value, SendOnlineRes.class);

        FlowFuture<SendOnlineRes> internetBankingFuture
                = f.invokeFunction(funcInternetBanking, input.value, SendOnlineRes.class);

        debitFuture.thenCompose((debitRes) -> internetBankingFuture.whenComplete((internetBankingRes, e) -> MessageReq.sendSuccessMessage(debitRes, internetBankingRes)
                ).exceptionallyCompose(
                        (e) -> cancel(funcCredit, input.value, e)
                ).exceptionally((err) -> {
                    MessageReq.sendFailMessage();
                    return null;
                })
        );

    }

    private static FlowFuture<SendOnlineRes> cancel(String cancelFn, Object input, Throwable e) {
        Flows.currentFlow().invokeFunction(cancelFn, input, SendOnlineRes.class);
        return Flows.currentFlow().failedFuture(e);
    }

}