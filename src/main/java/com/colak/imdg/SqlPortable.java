/*
 * Copyright 2021 Hazelcast Inc.
 *
 * Licensed under the Hazelcast Community License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://hazelcast.com/hazelcast-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.colak.imdg;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.jet.impl.util.Util;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;

public class SqlPortable  {


    private static final int PERSON_FACTORY_ID = 4;
    private static final int PERSON_CLASS_ID = 5;
    private static final int PERSON_CLASS_VERSION = 6;

    private static final int ALL_TYPES_FACTORY_ID = 7;
    private static final int ALL_TYPES_CLASS_ID = 8;
    private static final int ALL_TYPES_CLASS_VERSION = 9;

    private static final int EMPTY_TYPES_FACTORY_ID = 10;
    private static final int EMPTY_TYPES_CLASS_ID = 11;
    private static final int EMPTY_TYPES_CLASS_VERSION = 12;

    private static InternalSerializationService serializationService;
    private static ClassDefinition personClassDefinition;
    private static ClassDefinition emptyClassDefinition;

    // reusing ClassDefinitions as schema does not change
    public static void beforeClass(HazelcastInstance instance) {

        serializationService = Util.getSerializationService(instance);

        personClassDefinition =
                new ClassDefinitionBuilder(PERSON_FACTORY_ID, PERSON_CLASS_ID, PERSON_CLASS_VERSION)
                        .addIntField("id")
                        .addStringField("name")
                        .addStringField("ssn")
                        .build();
        serializationService.getPortableContext().registerClassDefinition(personClassDefinition);


        ClassDefinition allTypesValueClassDefinition =
                new ClassDefinitionBuilder(ALL_TYPES_FACTORY_ID, ALL_TYPES_CLASS_ID, ALL_TYPES_CLASS_VERSION)
                        .addCharField("character")
                        .addStringField("string")
                        .addBooleanField("boolean")
                        .addByteField("byte")
                        .addShortField("short")
                        .addIntField("int")
                        .addLongField("long")
                        .addFloatField("float")
                        .addDoubleField("double")
                        .addDecimalField("decimal")
                        .addTimeField("time")
                        .addDateField("date")
                        .addTimestampField("timestamp")
                        .addTimestampWithTimezoneField("timestampTz")
                        .addPortableField("object", personClassDefinition)
                        .build();
        serializationService.getPortableContext().registerClassDefinition(allTypesValueClassDefinition);

        emptyClassDefinition =
                new ClassDefinitionBuilder(EMPTY_TYPES_FACTORY_ID, EMPTY_TYPES_CLASS_ID, EMPTY_TYPES_CLASS_VERSION)
                        .build();
        serializationService.getPortableContext().registerClassDefinition(emptyClassDefinition);
    }


}
