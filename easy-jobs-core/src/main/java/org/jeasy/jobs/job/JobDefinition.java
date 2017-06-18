package org.jeasy.jobs.job;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDefinition {

        private int id;
        private String name;
        @JsonProperty("class")
        private String clazz;
        private String method;

        public JobDefinition() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return "Job {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", class='" + clazz + '\'' +
                    ", method='" + method + '\'' +
                    '}';
        }
    }