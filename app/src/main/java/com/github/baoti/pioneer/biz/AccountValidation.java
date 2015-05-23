/*
 * Copyright (c) 2014-2015 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer.biz;

import com.github.baoti.pioneer.biz.exception.ValidationException;
import com.github.baoti.pioneer.misc.util.Texts;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by sean on 2015/5/24.
 */
@Aspect
public class AccountValidation {

    @Pointcut("execution(* com.github.baoti.pioneer.biz.interactor.AccountInteractor+.signInDeferred(java.lang.String,java.lang.String))")
    public void accountInteractorSignInDeferred() {}

    @Before("accountInteractorSignInDeferred()")
    public void validate(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        validateAccount((String) args[0]);
    }

    private void validateAccount(String account) throws ValidationException {
        if (Texts.isTrimmedEmpty(account)) {
            throw new ValidationException(ValidationException.Kind.ACCOUNT_EMPTY);
        }
    }
}
