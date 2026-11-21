package net.dv8tion.jda.internal.requests;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;

@FunctionalInterface
public interface ErrorMapper {
   @Nullable
   Throwable apply(@Nonnull Response var1, @Nonnull Request<?> var2, @Nonnull ErrorResponseException var3);
}
