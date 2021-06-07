provider "alicloud" {
  #   access_key = "${var.access_key}"
  #   secret_key = "${var.secret_key}"
  region = "cn-hongkong"
}

variable "vs_zone" {
  default = "cn-hongkong-b"
}

#--------------------- 1. VPC

# Create VPC
resource "alicloud_vpc" "labex_vpc" {
  vpc_name   = "labex_vpc"
  cidr_block = "172.16.0.0/12"
}

# Create Vswitch
resource "alicloud_vswitch" "labex_vs" {
  vpc_id     = alicloud_vpc.labex_vpc.id
  cidr_block = "172.16.0.0/21"
  zone_id    = var.vs_zone
}

# Create security group
resource "alicloud_security_group" "default" {
  name        = "terraform-default"
  description = "terraform-default"
  vpc_id      = alicloud_vpc.labex_vpc.id
}

# Create Security group rule
resource "alicloud_security_group_rule" "allow_all_ssh" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "22/22"
  priority          = 1
  security_group_id = alicloud_security_group.default.id
  cidr_ip           = "0.0.0.0/0"
}

resource "alicloud_security_group_rule" "allow_all_web" {
  type              = "ingress"
  ip_protocol       = "tcp"
  nic_type          = "intranet"
  policy            = "accept"
  port_range        = "80/80"
  priority          = 1
  security_group_id = alicloud_security_group.default.id
  cidr_ip           = "0.0.0.0/0"
}

#--------------------- 2. RDS

variable "db_name" {
  default = "labex"
}

variable "db_user_password" {
  default = "Aliyun-test"
}

variable "db_user_name" {
  default = "labex"
}

variable "rds_type" {
  default = "rds.mysql.s2.large"
}

resource "alicloud_db_instance" "labex" {
  engine               = "MySQL"
  engine_version       = "5.7"
  instance_type        = var.rds_type
  instance_storage     = "30"
  instance_charge_type = "Postpaid"
  instance_name        = "labex"
  vswitch_id           = alicloud_vswitch.labex_vs.id
  monitoring_period    = "60"
  security_ips         = [alicloud_vswitch.labex_vs.cidr_block]
}

resource "alicloud_db_account" "account" {
  db_instance_id   = alicloud_db_instance.labex.id
  account_name     = var.db_user_name
  account_password = var.db_user_password
}

resource "alicloud_db_database" "database" {
  instance_id = alicloud_db_instance.labex.id
  name        = var.db_name
}

resource "alicloud_db_connection" "conn" {
  instance_id = alicloud_db_instance.labex.id
  #connection_prefix = "LabEx1982"
}

resource "alicloud_db_account_privilege" "privilege" {
  instance_id  = alicloud_db_instance.labex.id
  account_name = alicloud_db_account.account.name
  privilege    = "ReadWrite"
  db_names     = alicloud_db_database.database.*.name
}

# --------------------- 3. ECS

variable "ecs_type" {
  default = "ecs.hfc6.large"
}

variable "ecs_password" {
  default = "Aliyun-test"
}

# Create a server
resource "alicloud_instance" "labex" {
  image_id                   = "ubuntu_16_04_x64_20G_alibase_20210420.vhd"
  internet_charge_type       = "PayByBandwidth"
  internet_max_bandwidth_out = 5
  instance_type              = var.ecs_type
  system_disk_category       = "cloud_efficiency"
  security_groups            = [alicloud_security_group.default.id]
  instance_name              = "labex"
  vswitch_id                 = alicloud_vswitch.labex_vs.id
  password                   = var.ecs_password
}

resource "alicloud_instance" "labex2" {
  image_id                   = "ubuntu_16_04_x64_20G_alibase_20210420.vhd"
  internet_charge_type       = "PayByBandwidth"
  internet_max_bandwidth_out = 5
  instance_type              = var.ecs_type
  system_disk_category       = "cloud_efficiency"
  security_groups            = [alicloud_security_group.default.id]
  instance_name              = "labex2"
  vswitch_id                 = alicloud_vswitch.labex_vs.id
  password                   = var.ecs_password
}

# ---------------------- 4. Redis

resource "alicloud_kvstore_instance" "labex" {
  db_instance_name = "LabEx-redis"
  vswitch_id       = alicloud_vswitch.labex_vs.id
  security_ips     = [alicloud_vswitch.labex_vs.cidr_block]
  instance_type    = "Redis"
  engine_version   = "5.0"
  config = {
    appendonly             = "yes",
    lazyfree-lazy-eviction = "yes",
  }
  tags = {
    Created = "TF",
    For     = "Test",
  }
  resource_group_id = "rg-123456"
  zone_id           = var.vs_zone
  instance_class    = "redis.master.small.default"
  password          = "Aliyun-test"
}

# ---------------------- 5. Kafka

resource "alicloud_alikafka_instance" "labex" {
  name            = "LabEx-kafka"
  topic_quota     = "50"
  disk_type       = "1"
  disk_size       = "500"
  deploy_type     = "5"
  io_max          = "20"
  vswitch_id      = alicloud_vswitch.labex_vs.id
  service_version = "2.2.0"
}

resource "alicloud_alikafka_topic" "topic1" {
  instance_id   = alicloud_alikafka_instance.labex.id
  topic         = "topic1"
  local_topic   = "false"
  compact_topic = "false"
  partition_num = "6"
  remark        = "dafault_kafka_topic_remark"
}

resource "alicloud_alikafka_consumer_group" "group1" {
  consumer_id = "group1"
  instance_id = alicloud_alikafka_instance.labex.id
}

# ---------------------- Output 

output "ecs_id" {
  value = alicloud_instance.labex.id
}

output "ecs_id2" {
  value = alicloud_instance.labex2.id
}

output "vpc_id" {
  value = alicloud_vpc.labex_vpc.id
}

output "rds_id" {
  value = alicloud_db_instance.labex.id
}

output "redis_id" {
  value = alicloud_kvstore_instance.labex.id
}

output "kafka_id" {
  value = alicloud_alikafka_instance.labex.id
}
