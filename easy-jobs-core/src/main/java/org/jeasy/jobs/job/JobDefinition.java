package org.jeasy.jobs.job;

public class JobDefinition {

        private int id;
        private String name;
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
            return "JobDefinition {" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", class='" + clazz + '\'' +
                    ", method='" + method + '\'' +
                    '}';
        }
    }