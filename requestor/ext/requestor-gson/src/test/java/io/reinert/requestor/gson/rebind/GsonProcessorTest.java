/*
 * Copyright 2015 Danilo Reinert
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
package io.reinert.requestor.gson.rebind;

import java.util.Arrays;

import javax.tools.JavaFileObject;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

@RunWith(JUnit4.class)
public class GsonProcessorTest {

    @Test
    public void singleClass_CompilesSuccessfully() {
        JavaFileObject pojoFile = JavaFileObjects.forSourceLines("test.Animal",
                "package test;",
                "",
                "public class Animal {",
                "  private String name;",
                "",
                "  public String getName() { return name; }",
                "",
                "  public void setName(String name) { this.name = name; }",
                "}");
        JavaFileObject moduleFile = JavaFileObjects.forSourceLines("test.Module",
                "package test;",
                "",
                "import io.reinert.requestor.core.SerializationModule;",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "",
                "@GsonSerializationModule(Animal.class)",
                "public interface Module extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile, moduleFile))
                .processedWith(new GsonProcessor())
                .compilesWithoutError();
    }

    @Test
    public void multiClasses_CompilesSuccessfully() {
        JavaFileObject pojoFile1 = JavaFileObjects.forSourceLines("test.Animal",
                "package test;",
                "",
                "public class Animal {",
                "  private String name;",
                "",
                "  public String getName() { return name; }",
                "",
                "  public void setName(String name) { this.name = name; }",
                "}");
        JavaFileObject pojoFile2 = JavaFileObjects.forSourceLines("test.Kid",
                "package test;",
                "",
                "public class Kid {",
                "  private int age;",
                "",
                "  public int getAge() { return age; }",
                "",
                "  public void setAge(int age) { this.age = age; }",
                "}");
        JavaFileObject pojoFile3 = JavaFileObjects.forSourceLines("test.Birthday",
                "package test;",
                "",
                "public class Birthday {",
                "  private int date;",
                "",
                "  public int getDate() { return date; }",
                "",
                "  public void setDate(int date) { this.date = date; }",
                "}");
        JavaFileObject moduleFile = JavaFileObjects.forSourceLines("test.Module",
                "package test;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "",
                "@GsonSerializationModule({Animal.class, Kid.class, Birthday.class})",
                "public interface Module extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile1, pojoFile2, pojoFile3, moduleFile))
                .processedWith(new GsonProcessor())
                .compilesWithoutError();
    }

    @Test
    public void multiModules_CompilesSuccessfully() {
        JavaFileObject pojoFile1 = JavaFileObjects.forSourceLines("test.Animal",
                "package test;",
                "",
                "public class Animal {",
                "  private String name;",
                "",
                "  public String getName() { return name; }",
                "",
                "  public void setName(String name) { this.name = name; }",
                "}");
        JavaFileObject pojoFile2 = JavaFileObjects.forSourceLines("test.Kid",
                "package test;",
                "",
                "public class Kid {",
                "  private int age;",
                "",
                "  public int getAge() { return age; }",
                "",
                "  public void setAge(int age) { this.age = age; }",
                "}");
        JavaFileObject pojoFile3 = JavaFileObjects.forSourceLines("test.Birthday",
                "package test;",
                "",
                "public class Birthday {",
                "  private int date;",
                "",
                "  public int getDate() { return date; }",
                "",
                "  public void setDate(int date) { this.date = date; }",
                "}");
        JavaFileObject moduleFile1 = JavaFileObjects.forSourceLines("test.ModuleA",
                "package test;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "",
                "@GsonSerializationModule({Animal.class, Kid.class, Birthday.class})",
                "public interface ModuleA extends SerializationModule {}");
        JavaFileObject moduleFile2 = JavaFileObjects.forSourceLines("test.ModuleB",
                "package test;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "",
                "@GsonSerializationModule({Kid.class, Birthday.class})",
                "public interface ModuleB extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile1, pojoFile2, pojoFile3, moduleFile1, moduleFile2))
                .processedWith(new GsonProcessor())
                .compilesWithoutError();
    }

    @Test
    public void sameNameDifferentPackagesClasses_CompilesSuccessfully() {
        JavaFileObject pojoFile1 = JavaFileObjects.forSourceLines("test.Birthday",
                "package test;",
                "",
                "public class Birthday {",
                "  private int date;",
                "",
                "  public int getDate() { return date; }",
                "",
                "  public void setDate(int date) { this.date = date; }",
                "}");
        JavaFileObject pojoFile2 = JavaFileObjects.forSourceLines("other.Birthday",
                "package other;",
                "",
                "public class Birthday {",
                "  private int date;",
                "",
                "  public int getDate() { return date; }",
                "",
                "  public void setDate(int date) { this.date = date; }",
                "}");
        JavaFileObject moduleFile1 = JavaFileObjects.forSourceLines("ma.ModuleA",
                "package ma;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "import test.*;",
                "import other.*;",
                "",
                "@GsonSerializationModule({test.Birthday.class, other.Birthday.class})",
                "public interface ModuleA extends SerializationModule {}");
        JavaFileObject moduleFile2 = JavaFileObjects.forSourceLines("mb.ModuleB",
                "package mb;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "import test.*;",
                "",
                "@GsonSerializationModule(test.Birthday.class)",
                "public interface ModuleB extends SerializationModule {}");
        JavaFileObject moduleFile3 = JavaFileObjects.forSourceLines("ma.ModuleC",
                "package ma;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "import other.*;",
                "",
                "@GsonSerializationModule(other.Birthday.class)",
                "public interface ModuleC extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile1, pojoFile2, moduleFile1, moduleFile2, moduleFile3))
                .processedWith(new GsonProcessor())
                .compilesWithoutError();
    }

    @Test
    public void innerClass_CompilesSuccessfully() {
        JavaFileObject pojoFile = JavaFileObjects.forSourceLines("test.foo.Animal",
                "package test.foo;",
                "",
                "public class Animal {",
                "  private String name;",
                "",
                "  public String getName() { return name; }",
                "",
                "  public void setName(String name) { this.name = name; }",
                "",
                "  public static class Birthday {",
                "    private int date;",
                "",
                "    public int getDate() { return date; }",
                "",
                "    public void setDate(int date) { this.date = date; }",
                "  }",
                "}");
        JavaFileObject moduleFile = JavaFileObjects.forSourceLines("test.Module",
                "package test;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "import test.foo.*;",
                "",
                "@GsonSerializationModule({Animal.class, Animal.Birthday.class})",
                "public interface Module extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile, moduleFile))
                .processedWith(new GsonProcessor())
                .compilesWithoutError();
    }

    @Test
    public void notPublicClass_FailsToCompile() {
        JavaFileObject pojoFile = JavaFileObjects.forSourceLines("test.Animal",
                "package test;",
                "",
                "class Animal {",
                "  private String name;",
                "",
                "  public String getName() { return name; }",
                "",
                "  public void setName(String name) { this.name = name; }",
                "}");
        JavaFileObject moduleFile = JavaFileObjects.forSourceLines("test.Module",
                "package test;",
                "",
                "import io.reinert.requestor.gson.annotations.GsonSerializationModule;",
                "import io.reinert.requestor.core.SerializationModule;",
                "",
                "@GsonSerializationModule(Animal.class)",
                "public interface Module extends SerializationModule {}");
        assertAbout(javaSources()).that(Arrays.asList(pojoFile, moduleFile))
                .processedWith(new GsonProcessor())
                .failsToCompile()
                .withErrorContaining("class must be public")
                .in(moduleFile);
    }
}
