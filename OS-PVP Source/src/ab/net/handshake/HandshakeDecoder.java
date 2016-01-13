package ab.net.handshake;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import ab.net.login.LoginDecoder;
import ab.net.login.LoginEncoder;
import ab.net.login.RS2Decoder;
import ab.net.login.RS2Encoder;

/**
 * A {@link FrameDecoder} which decodes the handshake and makes changes to the
 * pipeline as appropriate for the selected service.
 * @author Graham
 */
public final class HandshakeDecoder extends FrameDecoder {

	/**
	 * Creates the handshake frame decoder.
	 */
	public HandshakeDecoder() {
		super(true);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (buffer.readable()) {
			int id = buffer.readUnsignedByte();

			switch (id) {
			case HandshakeConstants.SERVICE_GAME:
				System.out.println("gets");
				ctx.getPipeline().addLast("encoder", new LoginEncoder());
				ctx.getPipeline().addLast("decoder", new LoginDecoder());
				break;
			case HandshakeConstants.SERVICE_UPDATE:
				//ctx.getPipeline().addFirst("updateEncoder", new UpdateEncoder());
				//ctx.getPipeline().addBefore("handler", "updateDecoder", new UpdateDecoder());
				//ChannelBuffer buf = ChannelBuffers.buffer(8);
				//buf.writeLong(0);
				//channel.write(buf); // TODO should it be here?
				break;
			default:
				throw new Exception("Invalid service id");
			}

			ctx.getPipeline().remove(this);

			HandshakeMessage message = new HandshakeMessage(id);

			if (buffer.readable()) {
				return new Object[] { message, buffer.readBytes(buffer.readableBytes()) };
			} else {
				return message;
			}

		}
		return null;
	}

}
