/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.atlantisframework.jellyfish.http;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * ApiCounter
 *
 * @author Fred Feng
 * @since 2.0.1
 */
@Getter
@Setter
@ToString
public class ApiCounter implements Serializable, Cloneable {

	private static final long serialVersionUID = -8870925173668637122L;
	private long count;
	private long failedCount;
	private long timeoutCount;

	public ApiCounter() {
	}

	public ApiCounter(long count, long failedCount, long timeoutCount) {
		this.count = count;
		this.failedCount = failedCount;
		this.timeoutCount = timeoutCount;
	}

	public long getSuccessCount() {
		return count - failedCount - timeoutCount;
	}

	public ApiCounter clone() {
		try {
			return (ApiCounter) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}
