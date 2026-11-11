package com.SpringAI.ChatBot.aiTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {

    @Tool(description = """
            Performs arithmetic calculations
            Support operations: add, subtract, multiply, divide, mod, power.
            """)
    public double calculate(
            @ToolParam(description = "Operations: add, subtract, divide, multiply, mod, power")
            String operation,
            @ToolParam(description = "First number")
            double a,
            @ToolParam(description = "Second number")
            double b
    ){
        System.out.println("Calculator tool called ");

        if (operation.equalsIgnoreCase("add")){
            return a + b;
        } else if (operation.equalsIgnoreCase("subtract")) {
            return a - b;
        } else if (operation.equalsIgnoreCase("divide")) {
            if (b == 0){
                throw new IllegalArgumentException("Cannot divide by 0");
            }
            return a / b;
        } else if (operation.equalsIgnoreCase("multiply")) {
            if (b == 0){
                throw new IllegalArgumentException("Cannot multiple by 0");
            }
            return a * b;
        } else if (operation.equalsIgnoreCase("mod")) {
            if (b == 0){
                throw new IllegalArgumentException("Cannot mod by 0");
            }
            return a % b;
        }
        else if (operation.equalsIgnoreCase("power")) {
            return Math.pow(a,b);
        }
        else {
            throw new IllegalArgumentException("Unsupported operation "+operation);
        }
    }
}
