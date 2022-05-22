package impl;

import api.Runner;

import java.util.Collection;
import java.util.Set;

public final class RunnerImpl implements Runner {

    private Scanner.ScanResult scanResult; // Can be reused for a lower memory consumption.
    private Processor.ProcessResult processResult; // ^^

    @Override
    public boolean scan() {
        final Scanner scanner = new Scanner();
        final Scanner.ScanResult result = scanner.scan();

        return (this.scanResult = result).successful();
    }

    @Override
    public boolean process() {
        final Processor processor = new Processor();
        final Processor.ProcessResult processResult = processor.process(scanResult);

        return (this.processResult = processResult).successful();
    }

    @Override
    public void cleanup() {
        for (Collection<?> c : Set.of(scanResult.beans(), scanResult.injects())) {
            c.clear();
        }
    }
}
