package com.minersstudios.wholib.registrable;

import com.minersstudios.wholib.key.ResourceKey;

public interface Registry<R extends Registrable<ResourceKey>> extends Registrar<R>, Iterable<R> {}
