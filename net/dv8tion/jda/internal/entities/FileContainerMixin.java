package net.dv8tion.jda.internal.entities;

import java.util.stream.Stream;
import net.dv8tion.jda.api.utils.FileUpload;

public interface FileContainerMixin {
   Stream<FileUpload> getFiles();
}
