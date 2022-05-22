package impl;

import api.Runner;

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
        scanResult.beans().clear();
        scanResult.injects().clear();
        processResult.values().clear();
    }
}
