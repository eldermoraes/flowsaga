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
package com.eldermoraes.fn.messages;

import com.fnproject.fn.api.flow.Flows;

import java.io.Serializable;

public class MessageReq implements Serializable {
    public String message;

    public static void setFuncMessage(String funcMessage) {
        MessageReq.funcMessage = funcMessage;
    }

    static private String funcMessage;

    public static void sendFailMessage() {
        System.out.println("----> sendFailMessage : " + funcMessage);
        Flows.currentFlow().invokeFunction(funcMessage, composeFailMessage());
    }

    public static void sendSuccessMessage(WithdrawalRes debitRes, WithdrawalRes atmRes) {
        Flows.currentFlow().invokeFunction(funcMessage, composeSuccessMessage(debitRes, atmRes));
    }

    public static MessageReq composeSuccessMessage(WithdrawalRes debitResponse,
                                               WithdrawalRes atmResponse) {
        MessageReq result = new MessageReq();
        result.message = "Cash withdrawal confirmation: " + debitResponse.confirmation + "\n" +
                "ATM confirmation: " +  atmResponse.confirmation ;
        return result;
    }


    public static MessageReq composeFailMessage() {
        MessageReq result = new MessageReq();
        result.message = "We failed to perform you withdrawal, sorry.";
        return result;
    }

}
