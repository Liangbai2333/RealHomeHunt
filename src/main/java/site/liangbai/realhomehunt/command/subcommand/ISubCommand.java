package site.liangbai.realhomehunt.command.subcommand;

import org.bukkit.command.CommandSender;

public interface ISubCommand {
    void execute(CommandSender sender, String label, String[] args);
}
