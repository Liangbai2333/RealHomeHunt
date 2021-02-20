package site.liangbai.realhomehunt.command.subtabcompiler;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ISubTabCompiler {
    List<String> handle(CommandSender sender, int length, String[] args);
}