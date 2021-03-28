package site.liangbai.realhomehunt.processor;

import site.liangbai.realhomehunt.processor.impl.GunHitBlockProcessorImpl;

public final class Processors {
    public static final IGunHitBlockProcessor GUN_HIT_BLOCK_PROCESSOR = new GunHitBlockProcessorImpl();
}
