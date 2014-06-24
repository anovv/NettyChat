import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<String>{

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		System.out.println("Server exception: " + e.toString());
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception{
		//handles new connected client
		Channel incoming = ctx.channel();
		//notify other users in a group
		for(Channel channel : channels){
			if(channel != incoming){
				channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
			}
		}
		//add to a group
		channels.add(incoming);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
		//handles leaving client
		Channel incoming = ctx.channel();
		//notify other users in a group
		for(Channel channel : channels){
			if(channel != incoming){
				channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " has left!\n");
			}
		}
		//remove from a group
		channels.remove(incoming);
		
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String message) throws Exception {
		Channel incoming = arg0.channel();
		//notifying all channels except this one
		for(Channel channel : channels){
			if(channel != incoming){
				channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + message + "\n");
			}
		}
	}
}