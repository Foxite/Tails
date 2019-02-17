package uk.kihira.tails.common.network;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import uk.kihira.tails.common.LibraryEntryData;
import uk.kihira.tails.common.Tails;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.List;

public class LibraryEntriesMessage implements IMessage {

    private List<LibraryEntryData> entries;
    private boolean delete; //Only used when sending to server

    public LibraryEntriesMessage() {}
    public LibraryEntriesMessage(List<LibraryEntryData> entries, boolean delete) {
        this.entries = entries;
        this.delete = delete;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        String dataJson = ByteBufUtils.readUTF8String(buf);
        try {
            entries = Tails.gson.fromJson(dataJson, new TypeToken<List<LibraryEntryData>>() {}.getType());
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        delete = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, Tails.gson.toJson(entries, new TypeToken<List<LibraryEntryData>>() {}.getType()));
        buf.writeBoolean(delete);
    }

    public static class Handler implements IMessageHandler<LibraryEntriesMessage, IMessage> {

        @Override
        public IMessage onMessage(LibraryEntriesMessage message, MessageContext ctx) {
            //Client
            if (ctx.side.isClient()) {
                //Yeah this isn't exactly a nice way of doing this.
                for (LibraryEntryData entry : message.entries) {
                    entry.remoteEntry = true;
                }

                //We add server entries to the uk.kihira.tails.client
                Tails.proxy.getLibraryManager().removeRemoteEntries();
                Tails.proxy.getLibraryManager().addEntries(message.entries);
            }
            //Server
            else {
                if (message.delete) {
                    Tails.logger.debug("Removing Library Entries: " + message.entries.size());
                    Tails.proxy.getLibraryManager().libraryEntries.removeAll(message.entries);
                }
                else {
                    Tails.logger.debug("Adding Library Entries: " + message.entries.size());
                    Tails.proxy.getLibraryManager().addEntries(message.entries);
                }
                Tails.proxy.getLibraryManager().saveLibrary();
            }
            return null;
        }
    }
}
