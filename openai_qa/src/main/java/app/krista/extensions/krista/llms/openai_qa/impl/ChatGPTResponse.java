/*
 * Openai Llm Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.krista.llms.openai_qa.impl;

import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;


public class ChatGPTResponse {

    public String id;
    public String object;
    public long created;
    public String model;
    public List<Choice> choices;

    static public class Choice {

        static public class Message {

            public String role;
            public String content;

            public String getRole() {
                return role;
            }

            public String getContent() {
                return content;
            }

            public String toString() {
                return ReflectionToStringBuilder.toString(this);
            }

        }
        public Message message;
        public String finish_reason;
        public int index;

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finish_reason;
        }

        public int getIndex() {
            return index;
        }

        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public long getCreated() {
        return created;
    }

    public String getModel() {
        return model;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public String getAnswer() {
        if (choices != null && choices.size() > 0)
            return choices.getFirst().getMessage().getContent();
        return "N/A";
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
