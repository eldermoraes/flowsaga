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
import com.eldermoraes.fn.messages.WithdrawalReq;
import com.eldermoraes.fn.messages.WithdrawalRes;
import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.FnFeature;
import com.fnproject.fn.api.RuntimeContext;
import com.fnproject.fn.api.flow.Flow;
import com.fnproject.fn.api.flow.FlowFuture;
import com.fnproject.fn.api.flow.Flows;
import com.fnproject.fn.runtime.flow.FlowFeature;
import java.io.Serializable;

@FnFeature(FlowFeature.class)
public class WithdrawalFunction implements Serializable {

    String funcDebit;
    String funcCredit;
    String funcAtm;

    @FnConfiguration
    public void configure(RuntimeContext ctx) {
        funcDebit = ctx.getConfigurationByKey("DEBIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

        funcCredit = ctx.getConfigurationByKey("CREDIT-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

        funcAtm = ctx.getConfigurationByKey("ATM-ID")
                .orElseThrow(() -> new RuntimeException("Missing FunctionId"));

    }

    public void withdrawal(WithdrawalReq input) {

        Flow f = Flows.currentFlow();

        FlowFuture<WithdrawalRes> debitFuture
                = f.invokeFunction(funcDebit, input.value, WithdrawalRes.class);

        FlowFuture<WithdrawalRes> atmFuture
                = f.invokeFunction(funcAtm, input.value, WithdrawalRes.class);

        debitFuture.thenCompose((debitRes) -> atmFuture.whenComplete((atmRes, e) -> MessageReq.sendSuccessMessage(debitRes, atmRes)
                ).exceptionallyCompose(
                        (e) -> cancel(funcCredit, input.value, e)
                ).exceptionally((err) -> {
                    MessageReq.sendFailMessage();
                    return null;
                })
        );

    }

    private static FlowFuture<WithdrawalRes> cancel(String cancelFn, Object input, Throwable e) {
        Flows.currentFlow().invokeFunction(cancelFn, input, WithdrawalRes.class);
        return Flows.currentFlow().failedFuture(e);
    }

}
