package LockIt.command;

import LockIt.LockIt;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/**
 * @author CreeperFace
 */
public abstract class BaseCommand extends Command {

    protected LockIt api;
    protected Server server;

    public BaseCommand(String name, LockIt api) {
        super(name);
        this.description = "GT command";
        this.usageMessage = "";
        this.api = api;
    }

    protected LockIt getAPI() {
        return api;
    }

    @Override
    public abstract boolean execute(CommandSender sender, String label, String[] args);
}
