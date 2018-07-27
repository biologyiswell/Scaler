package test.io.github.biologyiswell.scaler.component;

public class Engineer {

    private String name;
    private int age;

    private String city;
    private String phone;
    private String address;

    public static EngineerBuilder builder() {
        return new EngineerBuilder();
    }

    public static class EngineerBuilder {

        private String name;
        private int age;

        private String city;
        private String phone;
        private String address;

        private EngineerBuilder() {
        }

        public EngineerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EngineerBuilder age(int age) {
            this.age = age;
            return this;
        }

        public EngineerBuilder city(String city) {
            this.city = city;
            return this;
        }

        public EngineerBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public EngineerBuilder address(String address) {
            this.address = address;
            return this;
        }

        public Engineer build() {
            Engineer engineer = new Engineer();
            engineer.name = this.name;
            engineer.age = this.age;
            engineer.city = this.city;
            engineer.phone = this.phone;
            engineer.address = this.address;

            return engineer;

        }
    }
}
