-- V008: Activate seed zones for development
UPDATE zones SET status = 'active' WHERE status = 'inactive';
