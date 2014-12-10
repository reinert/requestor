/*
 * Copyright 2014 Danilo Reinert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reinert.tools.checkstyle;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class IfBracesCheck extends Check {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
                TokenTypes.LITERAL_ELSE,
                TokenTypes.LITERAL_IF,
        };
    }

    @Override
    public void visitToken(DetailAST aAST) {
        final DetailAST slistAST = aAST.findFirstToken(TokenTypes.SLIST);

        if(aAST.getType() == TokenTypes.LITERAL_ELSE) {
            // If we have an else, it must have braces, except it is an "else if" (then the if must have braces).
            DetailAST ifToken = aAST.findFirstToken(TokenTypes.LITERAL_IF);

            if(ifToken == null) {
                // This is an simple else, it must have brace.
                if(slistAST == null) {
                    log(aAST.getLineNo(), "ifBracesElse", aAST.getText());
                }
            } else {
                // This is an "else if", the if must have braces.
                if(ifToken.findFirstToken(TokenTypes.SLIST) == null) {
                    log(aAST.getLineNo(), "ifBracesConditional", ifToken.getText(), aAST.getText() + " " + ifToken.getText());
                }
            }
        } else if(aAST.getType() == TokenTypes.LITERAL_IF) {
            // If the if uses braces, nothing as to be checked.
            if (slistAST != null) {
                return;
            }

            // We have an if, we need to check if it has no conditionnal structure as direct child.
            final int[] conditionals = {
                    TokenTypes.LITERAL_DO,
                    TokenTypes.LITERAL_ELSE,
                    TokenTypes.LITERAL_FOR,
                    TokenTypes.LITERAL_IF,
                    TokenTypes.LITERAL_WHILE,
                    TokenTypes.LITERAL_SWITCH,
            };

            for(int conditional : conditionals) {
                DetailAST conditionalAST = aAST.findFirstToken(conditional);

                if (conditionalAST != null) {
                    log(aAST.getLineNo(), "ifBracesConditional", aAST.getText(), conditionalAST.getText());

                    // Let's trigger this only once.
                    return;
                }
            }
        }
    }
}
