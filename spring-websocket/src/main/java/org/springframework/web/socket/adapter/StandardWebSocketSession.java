/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.socket.adapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * A {@link WebSocketSession} for use with the standard WebSocket for Java API.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class StandardWebSocketSession extends AbstractWebSocketSesssion<javax.websocket.Session> {

	private final HttpHeaders handshakeHeaders;

	private final InetSocketAddress localAddress;

	private final InetSocketAddress remoteAddress;


	/**
	 * Class constructor.
	 *
	 * @param handshakeHeaders the headers of the handshake request
	 * @param handshakeAttributes attributes from the HTTP handshake to make available
	 *        through the WebSocket session
	 * @param localAddress the address on which the request was received
	 * @param remoteAddress the address of the remote client
	 */
	public StandardWebSocketSession(HttpHeaders handshakeHeaders, Map<String, Object> handshakeAttributes,
			InetSocketAddress localAddress, InetSocketAddress remoteAddress) {

		super(handshakeAttributes);
		handshakeHeaders = (handshakeHeaders != null) ? handshakeHeaders : new HttpHeaders();
		this.handshakeHeaders = HttpHeaders.readOnlyHttpHeaders(handshakeHeaders);
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public String getId() {
		checkNativeSessionInitialized();
		return getNativeSession().getId();
	}

	@Override
	public URI getUri() {
		checkNativeSessionInitialized();
		return getNativeSession().getRequestURI();
	}

	@Override
	public HttpHeaders getHandshakeHeaders() {
		return this.handshakeHeaders;
	}

	@Override
	public Principal getPrincipal() {
		checkNativeSessionInitialized();
		return getNativeSession().getUserPrincipal();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return this.localAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return this.remoteAddress;
	}

	@Override
	public String getAcceptedProtocol() {
		checkNativeSessionInitialized();
		String protocol = getNativeSession().getNegotiatedSubprotocol();
		return StringUtils.isEmpty(protocol)? null : protocol;
	}

	@Override
	public boolean isOpen() {
		return ((getNativeSession() != null) && getNativeSession().isOpen());
	}

	@Override
	protected void sendTextMessage(TextMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendText(message.getPayload(), message.isLast());
	}

	@Override
	protected void sendBinaryMessage(BinaryMessage message) throws IOException {
		getNativeSession().getBasicRemote().sendBinary(message.getPayload(), message.isLast());
	}

	@Override
	protected void closeInternal(CloseStatus status) throws IOException {
		getNativeSession().close(new CloseReason(CloseCodes.getCloseCode(status.getCode()), status.getReason()));
	}

}
