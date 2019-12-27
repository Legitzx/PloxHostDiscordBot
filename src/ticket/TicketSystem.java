package ticket;


import core.Main;
import me.legitzx.bot.Info;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.xml.soap.Text;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;

/*
-new
Awaiting Client Reply
Awaiting Staff Reply

-management
Awaiting Management Reply
Awaiting Client Reply (mang.)
 */

public class TicketSystem extends ListenerAdapter {
    Main main = new Main();
    public int warnings;
    public int warning = 1;

    public HashMap<String, String> ids = new HashMap<>();
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");

        Document found = (Document) main.collection.find(new Document("id", event.getAuthor().getId())).first();

        if(event.getAuthor().isBot()) return;

        Role staff = event.getGuild().getRolesByName("Staff", true).get(0);
        Role management = event.getGuild().getRolesByName("Management", true).get(0);
        Role publc = event.getGuild().getPublicRole();


        if(args[0].equalsIgnoreCase(Info.PREFIX + "management")) {
            if(event.getMember().getRoles().contains(staff)) {
                if(event.getChannel().getName().startsWith("ticket")) {
                    Category man = event.getGuild().getCategoriesByName("Awaiting Management Reply", true).get(0);


                    EnumSet<Permission> allow1 = EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
                    EnumSet<Permission> deny1 = EnumSet.of(Permission.MESSAGE_TTS);


                    event.getChannel().getManager().removePermissionOverride(staff).putPermissionOverride(management, allow1, deny1).queue();
                    event.getChannel().getManager().setParent(man).queue();

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.CYAN);
                    builder.setTitle("Moved to Management Section");
                    event.getChannel().sendMessage(builder.build()).queue();
                } else {
                    event.getChannel().sendMessage("You must be in a ticket to do -management!").queue();
                }
            } else {
                event.getChannel().sendMessage("Invalid Perms").queue();
            }
        }

        //staff section
        if(event.getMember().getRoles().contains(staff)) {
            if(event.getChannel().getName().startsWith("ticket")) {
                if(!args[0].equalsIgnoreCase("-management")) {
                    Category man1 = event.getGuild().getCategoriesByName("Awaiting Staff Reply", true).get(0);
                    if (event.getChannel().getParent().equals(man1)) {
                        if (event.getAuthor().isBot()) return;
                        Category man = event.getGuild().getCategoriesByName("Awaiting Client Reply", true).get(0);
                        event.getChannel().getManager().setParent(man).queue();
                    }
                }
            }
        }

        if(!event.getMember().getRoles().contains(staff) || event.getMember().getRoles().contains(management)) {
            if(event.getChannel().getName().startsWith("ticket")) {
                Category man1 = event.getGuild().getCategoriesByName("Awaiting Client Reply", true).get(0);
                if (event.getChannel().getParent().equals(man1)) {
                    if(event.getAuthor().isBot()) return;
                    Category man = event.getGuild().getCategoriesByName("Awaiting Staff Reply", true).get(0);
                    event.getChannel().getManager().setParent(man).queue();
                }
            }
        }

        //management section
        if(event.getMember().getRoles().contains(management)) {
            if(event.getChannel().getName().startsWith("ticket")) {
                if(!args[0].equalsIgnoreCase("-management")) {
                    Category man1 = event.getGuild().getCategoriesByName("Awaiting Management Reply", true).get(0);
                    if (event.getChannel().getParent().equals(man1)) {
                        if (event.getAuthor().isBot()) return;
                        Category man = event.getGuild().getCategoriesByName("Awaiting Client Reply (mang.)", true).get(0);
                        event.getChannel().getManager().setParent(man).queue();
                    }
                }
            }
        }

        if(!event.getMember().getRoles().contains(management)) {
            if(event.getChannel().getName().startsWith("ticket")) {
                Category man1 = event.getGuild().getCategoriesByName("Awaiting Client Reply (mang.)", true).get(0);
                if (event.getChannel().getParent().equals(man1)) {
                    if(event.getAuthor().isBot()) return;
                    Category man = event.getGuild().getCategoriesByName("Awaiting Management Reply", true).get(0);
                    event.getChannel().getManager().setParent(man).queue();
                }
            }
        }

        if(args[0].equalsIgnoreCase(Info.PREFIX + "new")) {
            if(found != null) {
                System.out.println("Found user");
                Bson updatedvalue = new Document("warnings", 1);
                Bson updateoperation = new Document("$inc", updatedvalue);
                main.collection.updateOne(found, updateoperation);
                System.out.println("User Updated!");
                warning = found.getInteger("warnings");
                warning = warning + 1;

                System.out.println(warning);
            } else {
                warnings = warnings + 1;
                System.out.println("didnt find user, creating user now");
                Document document = new Document("id", event.getAuthor().getId());
                document.append("warnings", warnings);

                main.collection.insertOne(document);
                warnings = warnings - 1;
            }
            if(warning <= 22) {
                Role role = event.getGuild().getPublicRole();
                EnumSet<Permission> allow = EnumSet.of(Permission.MESSAGE_WRITE);
                EnumSet<Permission> deny = EnumSet.of(Permission.MESSAGE_READ);

                Role mod = event.getGuild().getRolesByName("Staff", true).get(0);
                EnumSet<Permission> allow1 = EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
                EnumSet<Permission> deny1 = EnumSet.of(Permission.MESSAGE_TTS);
                //create text channel add staff role and send message
                EnumSet<Permission> allow4 = EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ);
                EnumSet<Permission> deny4 = EnumSet.of(Permission.MESSAGE_TTS);
                String channelName = "ticket-" + event.getMember().getUser().getName();
                Category category = event.getGuild().getCategoriesByName("Awaiting Staff Reply", true).get(0);
                event.getGuild().getController().createTextChannel(channelName).setParent(category).addPermissionOverride(role, allow, deny).addPermissionOverride(mod, allow1, deny1).addPermissionOverride(event.getMember(), allow4, deny4).queue();



                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Your ticket has been created, Visit it below!");
                builder.addField("Ticket ID", "#ticket-" + event.getMember().getUser().getName().toLowerCase(), true);
                builder.setColor(Color.CYAN);
                event.getChannel().sendMessage(builder.build()).queue();
                //try to send this message to the ticket

                int counter = 0;
                while(counter < 50222) {
                    counter++;
                    if(counter == 50222) {
                        System.out.println("Initialized");
                        TextChannel channel2 = event.getGuild().getTextChannelsByName("ticket-" + event.getMember().getUser().getName(), true).get(0);
                        EmbedBuilder builder1 = new EmbedBuilder();
                        builder1.setTitle("Thank you for contacting PloxHost! We appreciate your inquiry! To make your support trip faster, please leave your question below.\n");
                        builder1.setColor(Color.CYAN);
                        channel2.sendMessage(builder1.build()).queue();
                    }
                }



                //channel2.sendMessage(builder1.build()).queue();









                warning = 0;
            } else {
                event.getChannel().sendMessage("You have reached 2 tickets " + event.getMember().getAsMention() + "!").queue();
                warning = 0;
            }
        }
        /*
        if(args[0].equalsIgnoreCase(Info.PREFIX + "close")) {


            if (event.getMember().getRoles().contains(staff) || event.getMember().getRoles().contains(management)) {
                if(event.getChannel().getName().startsWith("ticket")) {
                    closeEmbed(event.getChannel().getName(), event.getMember(), event.getGuild().getTextChannelById("501198087205945364"));
                    event.getChannel().delete().queue();
                } else {
                    event.getChannel().sendMessage("You cannot close a main channel!").queue();
                }
            } else {
                event.getChannel().sendMessage("Looks like you don't have perms to execute ```-close```").queue();
            }
        }
        */

    }



    public void closeEmbed(String channel1, Member admin, TextChannel channel) {
        EmbedBuilder builder = new EmbedBuilder();
        Date date = new Date(System.currentTimeMillis());
        builder.setTitle("Ticket Closed");
        builder.addField(channel1 + " was closed by: ", admin.getAsMention(), false);
        builder.setFooter("Ticket was closed on: " + date + ".", "https://cdn.discordapp.com/attachments/481991909342969869/495765001135587338/ottermcs.png");
        builder.setColor(Color.ORANGE);
        channel.sendMessage(builder.build()).queue();

    }



}
