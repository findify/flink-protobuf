package io.findify.flinkpb;

import io.findify.flinkprotobuf.java.Tests;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

public class JobTest {
    public static List<Tests.Foo> test(StreamExecutionEnvironment env) throws Exception {
        TypeInformation<Tests.Foo> ti = FlinkProtobuf.generateJava(Tests.Foo.class, Tests.Foo.getDefaultInstance());
        return env.fromCollection(List.of(Tests.Foo.newBuilder().setValue(1).build()), ti).executeAndCollect(100);
    }
}
