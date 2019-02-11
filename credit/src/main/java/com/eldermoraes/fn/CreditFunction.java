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

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;

public class CreditFunction {

    
    @FnConfiguration
    public void configure(RuntimeContext ctx) {
        String accountApiURL = ctx.getConfigurationByKey("ACCOUNT_API_URL")
                .orElseThrow(() -> new RuntimeException("No URL endpoint was provided."));
        //TODO setup account API 
    }
    
    public static class CreditRequest{
        public Double value;
    }
    
    public static class CreditResponse{
        public String confirmation;
    }
    
    public CreditResponse credit(CreditRequest input){
        CreditResponse resp = new CreditResponse();
        resp.confirmation = "Account credited"; //TODO create an API
        return resp;
    }

}