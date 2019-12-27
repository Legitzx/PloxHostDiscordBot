package core;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import ticket.TicketSystem;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class Main extends ListenerAdapter {
    public String uri = "mongodb+srv://admin:theluch55@royalbot-fbwir.mongodb.net/admin";
    public MongoClientURI clientURI = new MongoClientURI(uri);
    public MongoClient mongoClient = new MongoClient(clientURI);

    public MongoDatabase mongoDatabase = mongoClient.getDatabase("PloxHost");
    public MongoCollection collection = mongoDatabase.getCollection("ticket");
    public static void main(String[] args)
            throws LoginException, RateLimitedException, InterruptedException {
        JDA jda = new JDABuilder(AccountType.BOT).setToken("NTAxMTY5NDUzOTcxNjY4OTkz.DqVeIQ.R1ml9Yx1uDUHEN_KwoGEoiDZk9U").buildBlocking();
        jda.addEventListener(new TicketSystem());
        jda.getPresence().setGame(Game.playing("-new"));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        String content = ((Message) message).getContentRaw();
        MessageChannel channel = event.getChannel();

        if (content.startsWith(".ping")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Ping: ");
            builder.setDescription(event.getJDA().getPing() + "ms");
            builder.setColor(Color.green);
            channel.sendMessage(builder.build()).queue();
        }
    }
}