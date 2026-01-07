package lk.ase.kavinda.islandlink.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private final String BACKUP_DIR = "backups/";

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void performDailyBackup() {
        try {
            createBackup("daily");
            cleanOldBackups("daily", 7); // Keep 7 days
        } catch (Exception e) {
            System.err.println("Daily backup failed: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 * * SUN") // Weekly on Sunday at 3 AM
    public void performWeeklyBackup() {
        try {
            createBackup("weekly");
            cleanOldBackups("weekly", 4); // Keep 4 weeks
        } catch (Exception e) {
            System.err.println("Weekly backup failed: " + e.getMessage());
        }
    }

    public void createBackup(String type) throws IOException, InterruptedException {
        // Create backup directory if it doesn't exist
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = String.format("%s_backup_%s_%s.sql", type, "islandlink_db", timestamp);
        String backupFilePath = BACKUP_DIR + backupFileName;

        // Extract database name from URL
        String dbName = extractDatabaseName(databaseUrl);

        // Create mysqldump command
        ProcessBuilder processBuilder = new ProcessBuilder(
            "mysqldump",
            "--host=localhost",
            "--user=" + username,
            "--password=" + password,
            "--single-transaction",
            "--routines",
            "--triggers",
            dbName
        );

        processBuilder.redirectOutput(new File(backupFilePath));
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Backup created successfully: " + backupFilePath);
        } else {
            throw new RuntimeException("Backup failed with exit code: " + exitCode);
        }
    }

    public void restoreBackup(String backupFilePath) throws IOException, InterruptedException {
        String dbName = extractDatabaseName(databaseUrl);

        ProcessBuilder processBuilder = new ProcessBuilder(
            "mysql",
            "--host=localhost",
            "--user=" + username,
            "--password=" + password,
            dbName
        );

        processBuilder.redirectInput(new File(backupFilePath));
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Database restored successfully from: " + backupFilePath);
        } else {
            throw new RuntimeException("Restore failed with exit code: " + exitCode);
        }
    }

    private void cleanOldBackups(String type, int keepCount) {
        File backupDir = new File(BACKUP_DIR);
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith(type + "_backup_"));

        if (backupFiles != null && backupFiles.length > keepCount) {
            // Sort by last modified date
            java.util.Arrays.sort(backupFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

            // Delete old backups
            for (int i = keepCount; i < backupFiles.length; i++) {
                if (backupFiles[i].delete()) {
                    System.out.println("Deleted old backup: " + backupFiles[i].getName());
                }
            }
        }
    }

    private String extractDatabaseName(String url) {
        // Extract database name from JDBC URL
        // Example: jdbc:mysql://localhost:3306/islandlink_db -> islandlink_db
        String[] parts = url.split("/");
        String dbPart = parts[parts.length - 1];
        return dbPart.split("\\?")[0]; // Remove query parameters
    }

    public BackupStatus getBackupStatus() {
        File backupDir = new File(BACKUP_DIR);
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));

        BackupStatus status = new BackupStatus();
        if (backupFiles != null) {
            status.totalBackups = backupFiles.length;
            if (backupFiles.length > 0) {
                java.util.Arrays.sort(backupFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                status.lastBackupDate = new java.util.Date(backupFiles[0].lastModified()).toString();
                status.lastBackupSize = backupFiles[0].length();
            }
        }

        return status;
    }

    public static class BackupStatus {
        public int totalBackups = 0;
        public String lastBackupDate = "Never";
        public long lastBackupSize = 0;
    }
}