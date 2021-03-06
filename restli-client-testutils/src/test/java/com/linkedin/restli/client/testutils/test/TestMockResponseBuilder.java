/*
   Copyright (c) 2014 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.client.testutils.test;


import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.RestLiResponseException;
import com.linkedin.restli.client.testutils.MockResponseBuilder;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.common.AllProtocolVersions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author kparikh
 */
public class TestMockResponseBuilder
{
  @Test
  public void testBuild()
  {
    MockResponseBuilder<Greeting> mockResponseBuilder = new MockResponseBuilder<Greeting>();
    Greeting greeting = new Greeting().setId(1L).setMessage("message");
    Map<String, String> headers = Collections.singletonMap("foo", "bar");
    RestLiResponseException restLiResponseException = EasyMock.createMock(RestLiResponseException.class);
    EasyMock.expect(restLiResponseException.getErrorSource()).andReturn("foo").once();
    EasyMock.replay(restLiResponseException);

    // build a response object using all the setters. This response does not make sense logically but the goal here
    // is to test that the builder is working correctly.

    mockResponseBuilder
        .setEntity(greeting)
        .setHeaders(headers)
        .setId("1")
        .setStatus(200)
        .setRestLiResponseException(restLiResponseException);

    Response<Greeting> response = mockResponseBuilder.build();

    // when we build the Response the ID is put into the headers
    Map<String, String> builtHeaders = new HashMap<String, String>(headers);
    builtHeaders.put(RestConstants.HEADER_ID, "1");
    builtHeaders.put(RestConstants.HEADER_RESTLI_PROTOCOL_VERSION, AllProtocolVersions.BASELINE_PROTOCOL_VERSION.toString());

    Assert.assertEquals(response.getEntity(), greeting);
    Assert.assertEquals(response.getHeaders(), builtHeaders);
    Assert.assertEquals(response.getStatus(), 200);
    Assert.assertEquals(response.getId(), "1");
    Assert.assertEquals(response.getError().getErrorSource(), "foo");
  }
}
